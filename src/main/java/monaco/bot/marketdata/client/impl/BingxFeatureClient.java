package monaco.bot.marketdata.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import monaco.bot.marketdata.client.interfaces.MarketDataClient;
import monaco.bot.marketdata.dto.AssetCandleDto;
import monaco.bot.marketdata.dto.AssetContractDataDto;
import monaco.bot.marketdata.dto.AssetPriceDataDto;
import monaco.bot.marketdata.dto.AssetPriceDto;
import monaco.bot.marketdata.dto.ChangeLeverageDto;
import monaco.bot.marketdata.dto.ChangeLeverageResponseDto;
import monaco.bot.marketdata.dto.LeverageSizeDto;
import monaco.bot.marketdata.dto.PeriodAssetPriceCandlesRequest;
import monaco.bot.marketdata.dto.SymbolConfigDto;
import monaco.bot.marketdata.dto.SymbolLeverageResponseDto;
import monaco.bot.marketdata.dto.bingx.BingxApiResponse;
import monaco.bot.marketdata.dto.bingx.BingxCandleData;
import monaco.bot.marketdata.dto.bingx.BingxCandleResponse;
import monaco.bot.marketdata.mapper.AssetContractMapper;
import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.service.interfaces.ExchangeService;
import monaco.bot.marketdata.util.EncryptDecryptGenerator;
import monaco.bot.marketdata.util.SignatureGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static monaco.bot.marketdata.util.Constants.BINGX_API_KEY_NAME;
import static monaco.bot.marketdata.util.Constants.END_TIME;
import static monaco.bot.marketdata.util.Constants.INTERVAL;
import static monaco.bot.marketdata.util.Constants.LEVERAGE;
import static monaco.bot.marketdata.util.Constants.LIMIT;
import static monaco.bot.marketdata.util.Constants.SIDE;
import static monaco.bot.marketdata.util.Constants.START_TIME;
import static monaco.bot.marketdata.util.Constants.SYMBOL;
import static monaco.bot.marketdata.util.Constants.TIMESTAMP;

@Slf4j
@Service("Bingx")
@RequiredArgsConstructor
public class BingxFeatureClient implements MarketDataClient {

    private final RestTemplate restTemplate;

    private final EncryptDecryptGenerator encryptDecryptGenerator;

    private final AssetContractMapper assetContractMapper;

    private final ExchangeService exchangeService;

    private final ObjectMapper objectMapper;

    private static final String SYMBOL_LEVERAGE_PATH = "/openApi/swap/v2/trade/leverage";

    private static final String ASSET_PRICE_PATH = "/openApi/swap/v1/ticker/price";

    private static final String ASSET_DETAILS_PATH = "/openApi/swap/v2/quote/contracts";

    private static final String CANDLE_ASSET_PRICE_PATH = "/openApi/swap/v3/quote/klines";

    @Value("${exchange-url.bingx-perpetual}")
    private String url;

    @Override
    public LeverageSizeDto getSymbolLeverage(String symbol, String encodedApiKey, String encodedSecretKey, String exchange) {
        String secretKey = encryptDecryptGenerator.decryptData(encodedSecretKey);
        String apiKey = encryptDecryptGenerator.decryptData(encodedApiKey);
        String parametersString = this.getAssetParamsString(symbol, secretKey);
        String requestUrl = this.getRequestUrl(SYMBOL_LEVERAGE_PATH, parametersString);
        HttpHeaders httpHeaders = this.addHttpHeaders(BINGX_API_KEY_NAME, apiKey);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        log.info("[TRADING BOT] Time: {} | Market-data-service | getSymbolLeverage" +
                        " | asset's name : {} | action: {}",
                Timestamp.from(Instant.now()), symbol, "get asset leverage size");
        LeverageSizeDto data = Objects.requireNonNull(restTemplate.exchange(
                requestUrl,
                HttpMethod.GET,
                entity,
                SymbolLeverageResponseDto.class).getBody()).getData();
        data.setSymbol(symbol);
        data.setExchange(exchange);
        return data;
    }

    @Override
    public ChangeLeverageDto updateSymbolLeverage(String symbol, Long leverage,
                                                  String side, String encodedApiKey, String encodedSecretKey) {
        String secretKey = encryptDecryptGenerator.decryptData(encodedSecretKey);
        String apiKey = encryptDecryptGenerator.decryptData(encodedApiKey);
        String parametersString = this.getAssetParamsString(symbol, side, leverage, secretKey);
        String requestUrl = this.getRequestUrl(SYMBOL_LEVERAGE_PATH, parametersString);
        HttpHeaders httpHeaders = this.addHttpHeaders(BINGX_API_KEY_NAME, apiKey);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        log.info("[TRADING BOT] Time: {} | Market-data-service | getSymbolLeverage" +
                        " | asset's name : {} | action: {}",
                Timestamp.from(Instant.now()), symbol, "get asset leverage size");
        ChangeLeverageDto data = Objects.requireNonNull(restTemplate.exchange(
                requestUrl,
                HttpMethod.POST,
                entity,
                ChangeLeverageResponseDto.class).getBody()).getData();
        data.setSymbol(symbol);
        return data;
    }

    @Override
    public List<SymbolConfigDto> getSymbolConfig(String symbol, String apiKey, String secretKey, String exchange) {
        return null;
    }

    @SneakyThrows
    public List<AssetPriceDto> getAssetsPrices(String encodedApiKey, String encodedSecretKey) {
        String secretKey = encryptDecryptGenerator.decryptData(encodedSecretKey);
        String apiKey = encryptDecryptGenerator.decryptData(encodedApiKey);
        String parametersString = this.getAssetParamsString("", secretKey);
        String requestUrl = this.getRequestUrl(ASSET_PRICE_PATH, parametersString);
        HttpHeaders httpHeaders = this.addHttpHeaders(BINGX_API_KEY_NAME, apiKey);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        String response = Objects.requireNonNull(restTemplate.exchange(
                requestUrl,
                HttpMethod.GET,
                entity,
                String.class).getBody());
        BingxApiResponse apiResponse = objectMapper.readValue(response, BingxApiResponse.class);
        if (apiResponse.getCode() != 0) {
            throw new RuntimeException("Error from Bingx API: " + apiResponse.getMsg());
        }
        List<AssetPriceDto> assetPrices = apiResponse.getData();
        log.info("[TRADING BOT] Time: {} | Market-data-service | get asset price | asset's name : {} | action: {}",
                Timestamp.from(Instant.now()), assetPrices, "get asset price");
        return assetPrices;
    }

    @SneakyThrows
    @Override
    public List<AssetContract> getAssetDetails(String encodedApiKey, String encodedSecretKey, String exchange) {
        String secretKey = encryptDecryptGenerator.decryptData(encodedSecretKey);
        String apiKey = encryptDecryptGenerator.decryptData(encodedApiKey);
        String parametersString = this.getAssetParamsString("", secretKey);
        String requestUrl = this.getRequestUrl(ASSET_DETAILS_PATH, parametersString);
        HttpHeaders httpHeaders = this.addHttpHeaders(BINGX_API_KEY_NAME, apiKey);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        log.info("[TRADING BOT] Time: {} | Market-data-service | getAssetDetails" +
                        " | action: {}",
                Timestamp.from(Instant.now()), "get asset details");
        AssetContractDataDto data = restTemplate.exchange(
                requestUrl,
                HttpMethod.GET,
                entity,
                AssetContractDataDto.class).getBody();
        return assetContractMapper.fromAssetDetailsDtoListToAssetContractList(
                data.getData(), exchangeService.getExchangeByName(exchange));
    }

    @SneakyThrows
    @Override
    public List<AssetCandleDto> getPeriodAssetPriceCandles(PeriodAssetPriceCandlesRequest request,
                                                           String encodedApiKey, String encodedSecretKey, String exchange) {
        List<AssetCandleDto> candles = new ArrayList<>();

        for (String symbol : request.getSymbols()) {
            try {
                String secretKey = encryptDecryptGenerator.decryptData(encodedSecretKey);
                String apiKey = encryptDecryptGenerator.decryptData(encodedApiKey);

                // Строим параметры запроса
                String parametersString = this.getAssetPriceCandlesParamsString(request, secretKey, symbol);
                String requestUrl = this.getRequestUrl(CANDLE_ASSET_PRICE_PATH, parametersString);

                HttpHeaders httpHeaders = this.addHttpHeaders(BINGX_API_KEY_NAME, apiKey);
                HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);

                log.info("[TRADING BOT] Time: {} | Market-data-service | getPeriodAssetPriceCandles | asset's params : {} | action: {}",
                        Timestamp.from(Instant.now()), request, "get period asset price candles");
                log.info("This is the link to get data: {}", requestUrl);
                ResponseEntity<String> responseEntity = restTemplate.exchange(
                        requestUrl,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

                String response = Objects.requireNonNull(responseEntity.getBody());
                log.info("Response of asset price candle: {}", response);

                // Парсим полный ответ в BingxApiResponse<CandleData[]>
                BingxCandleResponse apiResponse = objectMapper.readValue(response, BingxCandleResponse.class);
                if (apiResponse.getCode() != 0) {
                    throw new RuntimeException("Error from Bingx API: " + apiResponse.getMsg());
                }

                List<AssetCandleDto> assetCandles = new ArrayList<>();
                if (apiResponse.getData() != null && apiResponse.getData().length > 0) {
                    for (BingxCandleData data : apiResponse.getData()[0].getCandles()) {
                        AssetCandleDto dto = AssetCandleDto.builder()
                                .open(data.getOpen())
                                .close(data.getClose())
                                .high(data.getHigh())
                                .low(data.getLow())
                                .volume(data.getVolume())
                                .time(Timestamp.from(Instant.ofEpochMilli(data.getTime())))
                                .symbol(symbol)
                                .exchange(exchange)
                                .build();
                        assetCandles.add(dto);
                    }
                }

                candles.addAll(assetCandles);

            } catch (Exception e) {
                log.error("Failed to fetch candle data for symbol {}: {}", symbol, e.getMessage(), e);
                throw new RuntimeException("Failed to fetch candle data for symbol " + symbol, e);
            }
        }
        return candles.stream()
                .peek(asset -> {
                    Double openPrice = asset.getOpen();
                    Double closePrice = asset.getClose();
                    asset.setVolume(asset.getVolume() * ((openPrice + closePrice) / 2));
                })
                .peek(asset -> asset.setExchange(exchange))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private String getAssetPriceCandlesParamsString(PeriodAssetPriceCandlesRequest request,
                                                    String secretKey, String symbol) {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put(TIMESTAMP, "" + new Timestamp(System.currentTimeMillis()).getTime());
        parameters.put(SYMBOL, symbol);
        parameters.put(INTERVAL, request.getInterval());
        parameters.put(START_TIME, this.convertToMillisecs(request.getStartTime()) + "");
        parameters.put(END_TIME, this.convertToMillisecs(request.getEndTime()) + "");
        parameters.put(LIMIT, String.valueOf(request.getLimit()));
        String valueToDigest = this.getMessageToDigest(parameters);
        String signature = SignatureGenerator.generateSignature(secretKey, valueToDigest);
        return valueToDigest + "&signature=" + signature;
    }

    @SneakyThrows
    private String getAssetParamsString(String symbol, String side, Long leverage, String secretKey) {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put(TIMESTAMP, "" + new Timestamp(System.currentTimeMillis()).getTime());
        parameters.put(SYMBOL, symbol);
        parameters.put(SIDE, side);
        parameters.put(LEVERAGE, leverage.toString());
        String valueToDigest = this.getMessageToDigest(parameters);
        String signature = SignatureGenerator.generateSignature(secretKey, valueToDigest);
        return valueToDigest + "&signature=" + signature;
    }

    @SneakyThrows
    private String getAssetParamsString(String symbol, String secretKey) {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put(TIMESTAMP, "" + new Timestamp(System.currentTimeMillis()).getTime());
        parameters.put(SYMBOL, symbol);
        String valueToDigest = this.getMessageToDigest(parameters);
        String signature = SignatureGenerator.generateSignature(secretKey, valueToDigest);
        return valueToDigest + "&signature=" + signature;
    }

    private Long convertToMillisecs(LocalDateTime time) {
        return ZonedDateTime.of(time, ZoneId.systemDefault()).toInstant().toEpochMilli();
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

    private HttpHeaders addHttpHeaders(String apiName, String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(apiName, apiKey);
        return headers;
    }

}
