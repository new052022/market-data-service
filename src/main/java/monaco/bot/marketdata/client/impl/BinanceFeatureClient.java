package monaco.bot.marketdata.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import monaco.bot.marketdata.client.interfaces.MarketDataClient;
import monaco.bot.marketdata.dto.AssetCandleDto;
import monaco.bot.marketdata.dto.AssetPriceDto;
import monaco.bot.marketdata.dto.ChangeLeverageDto;
import monaco.bot.marketdata.dto.ChangeMarginTypeDto;
import monaco.bot.marketdata.dto.LeverageSizeDto;
import monaco.bot.marketdata.dto.PeriodAssetPriceCandlesRequest;
import monaco.bot.marketdata.dto.SymbolConfigDto;
import monaco.bot.marketdata.dto.binance.BracketDto;
import monaco.bot.marketdata.dto.binance.CandleStickDataDto;
import monaco.bot.marketdata.dto.binance.LeverageDto;
import monaco.bot.marketdata.dto.binance.exchangeInfo.AssetInfoDto;
import monaco.bot.marketdata.dto.binance.exchangeInfo.LeverageChangeResponseDto;
import monaco.bot.marketdata.mapper.AssetCandleConverter;
import monaco.bot.marketdata.mapper.AssetContractMapper;
import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.service.interfaces.ExchangeService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static monaco.bot.marketdata.util.Constants.BINANCE_API_KEY_NAME;
import static monaco.bot.marketdata.util.Constants.END_TIME;
import static monaco.bot.marketdata.util.Constants.INTERVAL;
import static monaco.bot.marketdata.util.Constants.LEVERAGE;
import static monaco.bot.marketdata.util.Constants.LIMIT;
import static monaco.bot.marketdata.util.Constants.MARGIN_TYPE;
import static monaco.bot.marketdata.util.Constants.RECV_WINDOW;
import static monaco.bot.marketdata.util.Constants.START_TIME;
import static monaco.bot.marketdata.util.Constants.SYMBOL;
import static monaco.bot.marketdata.util.Constants.TIMESTAMP;

@Slf4j
@Service("Binance")
@RequiredArgsConstructor
public class BinanceFeatureClient implements MarketDataClient {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final AssetCandleConverter candleConverter;

    private final AssetContractMapper assetContractMapper;

    private final EncryptDecryptGenerator encryptDecryptGenerator;

    private final ExchangeService exchangeService;

    @Value("${exchange-url.binance-perpetual.v1}")
    private String url;

    public static final String ASSET_CANDLE_PRICE_PATH = "/klines";

    public static String PRICE_PATH = "/ticker/price";

    public static final String LEVERAGE_BRACKET = "/leverageBracket";

    public static final String SYMBOLS_CONFIG = "/symbolConfig";

    public static final String EXCHANGE_INFO = "/exchangeInfo";

    public static final String LEVERAGE_PATH = "/leverage";

    public static final String MARGIN_TYPE_PATH = "/marginType";

    @Override
    @SneakyThrows
    public List<AssetPriceDto> getAssetsPrices(String apiKey, String secretKey) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url + PRICE_PATH);
       String response = restTemplate.exchange(
                uriBuilder.toUriString(), HttpMethod.GET, null, String.class).getBody();
        List<AssetPriceDto> assetPrices = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, AssetPriceDto.class));
        log.info("[TRADING BOT] Time: {} | Market-data-service | getAssetPrice" +
                        " |  Prices: {} | action: {}",
                Timestamp.from(Instant.now()), Objects.requireNonNull(assetPrices), "fetch price");
        return assetPrices;
    }

    @SneakyThrows
    @Override
    public List<AssetCandleDto> getPeriodAssetPriceCandles(PeriodAssetPriceCandlesRequest request, String apiKey,
                                                           String secretKey, String exchange) {
        List<CandleStickDataDto> data = new ArrayList<>();
        for(String symbol : request.getSymbols()) {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url + ASSET_CANDLE_PRICE_PATH)
                    .queryParam(SYMBOL, symbol)
                    .queryParam(INTERVAL, request.getInterval())
                    .queryParam(START_TIME, this.convertToMillisecs(request.getStartTime()))
                    .queryParam(END_TIME, this.convertToMillisecs(request.getEndTime()))
                    .queryParam(LIMIT, request.getLimit());
                log.info("URL for binance api to get candles: {}", uriBuilder.toUriString());
            try {
                String indexInfo = restTemplate.exchange(uriBuilder.toUriString(),
                        HttpMethod.GET,
                        null,
                        String.class).getBody();
                List<CandleStickDataDto> response = objectMapper.readValue(indexInfo, objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, CandleStickDataDto.class));
                response.forEach(candle -> candle.setSymbol(symbol));
                data.addAll(response);
                log.info("[TRADING BOT] Time: {} | Market-data-service | getPeriodAssetPriceCandles" +
                                " | asset's params : {} | action: {}",
                        Timestamp.from(Instant.now()), request, "get period asset price candles");
            } catch (Exception e) {
                log.error("Get period asset price candles failed with message {}", e.getMessage());
            }
        }
        return data.stream()
                .map(candleConverter::convertToAssetCandleDto)
                .peek(asset -> asset.setExchange(exchange))
                .collect(Collectors.toList());
    }

    @Override
        public List<AssetContract> getAssetDetails(String apiKey, String secretKey, String exchange) {
        Map<String, LeverageDto> leverageMap = this.getLeverages(apiKey,secretKey).stream()
                .collect(Collectors.toMap(LeverageDto::getSymbol, Function.identity()));
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url + EXCHANGE_INFO);
        return Objects.requireNonNull(restTemplate.exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.GET,
                        null,
                        AssetInfoDto.class).getBody()).getSymbols().stream()
                .map(symbol -> assetContractMapper.toAssetCandleDto(symbol, leverageMap.get(symbol.getSymbol()),
                        exchangeService.getExchangeByName(exchange)))
                .collect(Collectors.toList());
    }

    @Override
    public LeverageSizeDto getSymbolLeverage(String symbol, String apiKey, String secretKey, String exchange) {
        List<LeverageDto> leverages = this.getLeverages(apiKey, secretKey);
        Optional<LeverageDto> leverageResponse = leverages.stream()
                .filter(leverage -> leverage.getSymbol().equalsIgnoreCase(symbol))
                .findFirst();
        if (leverageResponse.isEmpty()) {
            return null;
        } else {
            Map<String, LeverageDto> leverageMap = leverages.stream()
                    .collect(Collectors.toMap(LeverageDto::getSymbol, Function.identity()));
            BracketDto leverageData = leverageMap.get(symbol).getBrackets().get(0);
           log.info("Get leverage response: {}", leverageData);
            return LeverageSizeDto.builder()
                    .symbol(symbol)
                    .maxLongLeverage(leverageData.getInitialLeverage().longValue())
                    .maxShortLeverage(leverageData.getInitialLeverage().longValue())
                    .minLongLeverage(leverageData.getBracket().longValue())
                    .minShortLeverage(leverageData.getBracket().longValue())
                    .exchange(exchange)
                    .build();
        }
    }

    @Override
    public ChangeLeverageDto updateSymbolLeverage(String symbol, Long leverage, String side, String encodedApiKey, String encodedSecretKey) {
        String time = "" + new Timestamp(System.currentTimeMillis()).getTime();
        String recvWindows = "15000";
        String secretKey = encryptDecryptGenerator.decryptData(encodedSecretKey);
        String apiKey = encryptDecryptGenerator.decryptData(encodedApiKey);
        String params = this.getAssetsUpdateString(secretKey, time, recvWindows, symbol, leverage);
        String requestUrl = this.getRequestUrl(LEVERAGE_PATH, params);
        HttpHeaders headers = this.addHttpHeaders(BINANCE_API_KEY_NAME, apiKey);
        LeverageChangeResponseDto leverageResponse = restTemplate.exchange(
                requestUrl, HttpMethod.POST, new HttpEntity<>(headers),
                LeverageChangeResponseDto.class).getBody();
        log.info("Leverage updating response: {}", leverageResponse);
        return ChangeLeverageDto.builder()
                .leverage(Objects.requireNonNull(leverageResponse).getLeverage())
                .symbol(leverageResponse.getSymbol())
                .build();
    }

    @Override
    public  List<SymbolConfigDto> getSymbolConfig(String symbol, String apiKey, String secretKey, String exchange) {
        List<SymbolConfigDto> leverages = this.getSymbolConfigResponse(apiKey, secretKey);
      return leverages.stream()
              .filter(symbolConfig -> !Objects.nonNull(symbol) ||
                      symbolConfig.getSymbol().equalsIgnoreCase(symbol))
              .peek(symbolConfig -> symbolConfig.setExchange(exchange))
              .toList();
    }

    @Override
    @SneakyThrows
    public ChangeMarginTypeDto updateMarginType(String symbol, String marginType, String encodedApiKey, String encodedSecretKey) {
        String time = "" + new Timestamp(System.currentTimeMillis()).getTime();
        String recvWindows = "15000";
        String secretKey = encryptDecryptGenerator.decryptData(encodedSecretKey);
        String apiKey = encryptDecryptGenerator.decryptData(encodedApiKey);
        String params = this.getMarginTypeUpdateString(secretKey, time, recvWindows, symbol, marginType);
        String requestUrl = this.getRequestUrl(MARGIN_TYPE_PATH, params);
        HttpHeaders headers = this.addHttpHeaders(BINANCE_API_KEY_NAME, apiKey);
        String marginResponse = restTemplate.exchange(
                requestUrl, HttpMethod.POST, new HttpEntity<>(headers), String.class).getBody();
        log.info("Margin type updating response: {}", marginResponse);
        ChangeMarginTypeDto response = objectMapper.readValue(marginResponse, ChangeMarginTypeDto.class);
        return ChangeMarginTypeDto.builder()
                .symbol(response.getSymbol())
                .marginType(response.getMarginType())
                .build();
    }

    @SneakyThrows
    private List<SymbolConfigDto> getSymbolConfigResponse(String encodedApiKey, String encodedSecretKey) {
        String time = "" + new Timestamp(System.currentTimeMillis()).getTime();
        String recvWindows = "15000";
        String secretKey = encryptDecryptGenerator.decryptData(encodedSecretKey);
        String apiKey = encryptDecryptGenerator.decryptData(encodedApiKey);
        String params = this.getAssetsLeverageString(secretKey, time, recvWindows);
        String requestUrl = this.getRequestUrl(SYMBOLS_CONFIG, params);
        HttpHeaders headers = this.addHttpHeaders(BINANCE_API_KEY_NAME, apiKey);
        String leverageResponse = restTemplate.exchange(
                requestUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        List<SymbolConfigDto> leverageList = objectMapper.readValue(leverageResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, SymbolConfigDto.class));
        log.info("[TRADING BOT] Time: {} | Market-data-service | getSymbolConfigResponse" +
                        " | number of assets' leverages : {} | action: {}",
                Timestamp.from(Instant.now()), leverageList.size(), "fetch assets' leverages");
        return leverageList;
    }

    @SneakyThrows
    public List<LeverageDto> getLeverages(String encodedApiKey, String encodedSecretKey) {
        String time = "" + new Timestamp(System.currentTimeMillis()).getTime();
        String recvWindows = "15000";
        String secretKey = encryptDecryptGenerator.decryptData(encodedSecretKey);
        String apiKey = encryptDecryptGenerator.decryptData(encodedApiKey);
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

    @SneakyThrows
    private String getMarginTypeUpdateString(String secretKey, String time, String recvWindow, String symbol,
                                         String marginType) {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put(TIMESTAMP, time);
        parameters.put(RECV_WINDOW, recvWindow);
        parameters.put(SYMBOL, symbol);
        parameters.put(MARGIN_TYPE, marginType);
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
