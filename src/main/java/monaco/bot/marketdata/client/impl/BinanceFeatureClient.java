package monaco.bot.marketdata.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import monaco.bot.marketdata.client.interfaces.MarketDataClient;
import monaco.bot.marketdata.dto.AssetCandleDto;
import monaco.bot.marketdata.dto.AssetPriceDto;
import monaco.bot.marketdata.dto.ChangeLeverageDto;
import monaco.bot.marketdata.dto.LeverageSizeDto;
import monaco.bot.marketdata.dto.PeriodAssetPriceCandlesRequest;
import monaco.bot.marketdata.dto.binance.CandleStickDataDto;
import monaco.bot.marketdata.dto.binance.LeverageDto;
import monaco.bot.marketdata.dto.binance.exchangeInfo.AssetInfoDto;
import monaco.bot.marketdata.dto.binance.exchangeInfo.LeverageChangeResponseDto;
import monaco.bot.marketdata.mapper.AssetCandleConverter;
import monaco.bot.marketdata.mapper.AssetContractMapper;
import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.model.UserExchangeInfo;
import monaco.bot.marketdata.util.EncryptDecryptGenerator;
import monaco.bot.marketdata.util.SignatureGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static monaco.bot.marketdata.util.Constants.*;
import static monaco.bot.marketdata.util.Constants.INTERVAL;

@Slf4j
@Service("Binance")
@RequiredArgsConstructor
public class BinanceFeatureClient implements MarketDataClient {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final AssetCandleConverter candleConverter;

    private final AssetContractMapper assetContractMapper;

    private final EncryptDecryptGenerator encryptDecryptGenerator;

    @Value("${exchange-url.binance-perpetual.v1}")
    private String url;

    public static final String ASSET_CANDLE_PRICE_PATH = "/klines";

    public static String PRICE_PATH = "/ticker/price";

    public static final String LEVERAGE_BRACKET = "/leverageBracket";

    public static final String EXCHANGE_INFO = "/exchangeInfo";

    public static final String LEVERAGE_PATH = "/leverage";

    @Override
    public AssetPriceDto getAssetPrice(String symbol, UserExchangeInfo exchangeInfo) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url + PRICE_PATH)
                .queryParam(SYMBOL, symbol);
        AssetPriceDto price = restTemplate.exchange(
                uriBuilder.toUriString(), HttpMethod.GET, null, AssetPriceDto.class).getBody();
        log.info("[TRADING BOT] Time: {} | Market-data-service | getAssetPrice" +
                        " |  Price: {} | action: {}",
                Timestamp.from(Instant.now()), Objects.requireNonNull(price).getPrice(), "fetch price");
        return price;
    }

    @SneakyThrows
    @Override
    public List<AssetCandleDto> getPeriodAssetPriceCandles(PeriodAssetPriceCandlesRequest request, UserExchangeInfo exchangeInfo) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url + ASSET_CANDLE_PRICE_PATH)
                .queryParam(SYMBOL, request.getSymbol())
                .queryParam(INTERVAL, request.getInterval())
                .queryParam(START_TIME, this.convertToMillisecs(request.getStartTime()))
                .queryParam(END_TIME, this.convertToMillisecs(request.getEndTime()))
                .queryParam(LIMIT, request.getLimit());
        String indexInfo = restTemplate.exchange(uriBuilder.toUriString(),
                HttpMethod.GET,
                null,
                String.class).getBody();
        List<CandleStickDataDto> data = objectMapper.readValue(indexInfo, objectMapper.getTypeFactory()
                .constructCollectionType(List.class, CandleStickDataDto.class));
        log.info("[TRADING BOT] Time: {} | Market-data-service | getPeriodAssetPriceCandles" +
                        " | asset's params : {} | action: {}",
                Timestamp.from(Instant.now()), request, "get period asset price candles");
        return data.stream()
                .map(candleConverter::convertToAssetCandleDto)
                .peek(asset -> asset.setSymbol(request.getSymbol()))
                .peek(asset -> asset.setExchange(exchangeInfo.getExchange().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetContract> getAssetDetails(UserExchangeInfo exchangeInfo) {
        Map<String, LeverageDto> leverageMap = this.getLeverages(exchangeInfo).stream()
                .collect(Collectors.toMap(LeverageDto::getSymbol, Function.identity()));
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url + EXCHANGE_INFO);
        return Objects.requireNonNull(restTemplate.exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.GET,
                        null,
                        AssetInfoDto.class).getBody()).getSymbols().stream()
                .map(symbol -> assetContractMapper.toAssetCandleDto(symbol, leverageMap.get(symbol.getSymbol()),
                        exchangeInfo.getExchange()))
                .collect(Collectors.toList());
    }

    @Override
    public LeverageSizeDto getSymbolLeverage(String symbol, UserExchangeInfo exchangeInfo) {
        List<LeverageDto> leverages = this.getLeverages(exchangeInfo);
        Optional<LeverageDto> leverageResponse = leverages.stream()
                .filter(leverage -> leverage.getSymbol().equalsIgnoreCase(symbol))
                .findFirst();
        if (leverageResponse.isEmpty()) {
            return null;
        } else {
            Map<String, LeverageDto> leverageMap = leverages.stream()
                    .collect(Collectors.toMap(LeverageDto::getSymbol, Function.identity()));
            long leverage = leverageMap.get(symbol).getBrackets().get(0).getInitialLeverage().longValue();
            return LeverageSizeDto.builder()
                    .symbol(symbol)
                    .maxLongLeverage(leverage)
                    .maxShortLeverage(leverage)
                    .longLeverage(leverage)
                    .shortLeverage(leverage)
                    .exchange(exchangeInfo.getExchange().getName())
                    .build();
        }
    }

    @Override
    public ChangeLeverageDto updateSymbolLeverage(String symbol, Long leverage, String side, UserExchangeInfo exchangeInfo) {
        String time = "" + new Timestamp(System.currentTimeMillis()).getTime();
        String recvWindows = "15000";
        String secretKey = encryptDecryptGenerator.decryptData(exchangeInfo.getSecretKey());
        String apiKey = encryptDecryptGenerator.decryptData(exchangeInfo.getApiKey());
        String params = this.getAssetsUpdateString(secretKey, time, recvWindows, symbol, leverage);
        String requestUrl = this.getRequestUrl(LEVERAGE_PATH, params);
        HttpHeaders headers = this.addHttpHeaders(BINANCE_API_KEY_NAME, apiKey);
        LeverageChangeResponseDto leverageResponse = restTemplate.exchange(
                requestUrl, HttpMethod.POST, new HttpEntity<>(headers),
                LeverageChangeResponseDto.class).getBody();
        return ChangeLeverageDto.builder()
                .leverage(Objects.requireNonNull(leverageResponse).getLeverage())
                .symbol(leverageResponse.getSymbol())
                .build();
    }

    @SneakyThrows
    public List<LeverageDto> getLeverages(UserExchangeInfo exchangeInfo) {
        String time = "" + new Timestamp(System.currentTimeMillis()).getTime();
        String recvWindows = "15000";
        String secretKey = encryptDecryptGenerator.decryptData(exchangeInfo.getSecretKey());
        String apiKey = encryptDecryptGenerator.decryptData(exchangeInfo.getApiKey());
        String params = this.getAssetsLeverageString(secretKey, time, recvWindows);
        String requestUrl = this.getRequestUrl(LEVERAGE_BRACKET, params);
        HttpHeaders headers = this.addHttpHeaders(BINANCE_API_KEY_NAME, apiKey);
        String leverageResponse = restTemplate.exchange(
                requestUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        List<LeverageDto> leverageList = objectMapper.readValue(leverageResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, LeverageDto.class));
        log.info("[TRADING BOT] Time: {} | Market-data-service | getLeverages" +
                        " | number of assets' leverages : {} | action: {}",
                Timestamp.from(Instant.now()), leverageList.size(), "fetch assets' leverages");
        return leverageList;
    }

    @SneakyThrows
    private String getAssetsLeverageString(String secretKey, String time, String recvWindow) {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put(TIMESTAMP, time);
        parameters.put(RECV_WINDOW, recvWindow);
        String valueToDigest = this.getMessageToDigest(parameters);
        String signature = SignatureGenerator.generateSignature(secretKey, valueToDigest);
        return valueToDigest + "&signature=" + signature;
    }

    @SneakyThrows
    private String getAssetsUpdateString(String secretKey, String time, String recvWindow, String symbol,
                                         Long leverage) {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put(TIMESTAMP, time);
        parameters.put(RECV_WINDOW, recvWindow);
        parameters.put(SYMBOL, symbol);
        parameters.put(LEVERAGE, leverage.toString());
        String valueToDigest = this.getMessageToDigest(parameters);
        String signature = SignatureGenerator.generateSignature(secretKey, valueToDigest);
        return valueToDigest + "&signature=" + signature;
    }

    private String getMessageToDigest(TreeMap<String, String> parameters) {
        Boolean first = true;
        String valueToDigest = "";
        for (Map.Entry<String, String> e : parameters.entrySet()) {
            if (!first) {
                valueToDigest += "&";
            }
            first = false;
            valueToDigest += e.getKey() + "=" + e.getValue();
        }
        return valueToDigest;
    }

    private String getRequestUrl(String path, String parameters) {
        String urlStr = url + path + "?" + parameters;
        return urlStr;
    }

    private Long convertToMillisecs(LocalDateTime time) {
        return ZonedDateTime.of(time, ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private HttpHeaders addHttpHeaders(String apiName, String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(apiName, apiKey);
        return headers;
    }

}
