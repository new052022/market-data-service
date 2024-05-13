package monaco.bot.marketdata.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import monaco.bot.marketdata.client.interfaces.MarketDataClient;
import monaco.bot.marketdata.dto.AssetCandleDto;
import monaco.bot.marketdata.dto.AssetContractDataDto;
import monaco.bot.marketdata.dto.AssetPriceDataDto;
import monaco.bot.marketdata.dto.AssetPriceDto;
import monaco.bot.marketdata.dto.PeriodAssetPriceCandlesRequest;
import monaco.bot.marketdata.dto.SingleAssetPriceDto;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static monaco.bot.marketdata.util.Constants.BINGX_API_KEY_NAME;
import static monaco.bot.marketdata.util.Constants.END_TIME;
import static monaco.bot.marketdata.util.Constants.INTERVAL;
import static monaco.bot.marketdata.util.Constants.LIMIT;
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

    private static final String ASSET_PRICE_PATH = "/openApi/swap/v1/ticker/price";

    private static final String ASSET_DETAILS_PATH = "/openApi/swap/v2/quote/contracts";

    private static final String CANDLE_ASSET_PRICE_PATH = "/openApi/swap/v3/quote/klines";

    @Value("${exchange-url.bingx-perpetual}")
    private String url;

    @SneakyThrows
    @Override
    public AssetPriceDto getAssetPrice(String symbol, UserExchangeInfo exchangeInfo) {
        String secretKey = encryptDecryptGenerator.decryptData(exchangeInfo.getSecretKey());
        String apiKey = encryptDecryptGenerator.decryptData(exchangeInfo.getApiKey());
        String parametersString = this.getAssetParamsString(symbol, secretKey);
        String requestUrl = this.getRequestUrl(ASSET_PRICE_PATH, parametersString);
        HttpHeaders httpHeaders = this.addHttpHeaders(BINGX_API_KEY_NAME, apiKey);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        log.info("[TRADING BOT] Time: {} | Market-data-service | get asset price" +
                        " | asset's name : {} | action: {}",
                Timestamp.from(Instant.now()), symbol, "get asset price");
        return Objects.requireNonNull(restTemplate.exchange(
                requestUrl,
                HttpMethod.GET,
                entity,
                SingleAssetPriceDto.class).getBody()).getData();
    }

    @SneakyThrows
    @Override
    public List<AssetContract> getAssetDetails(UserExchangeInfo exchangeInfo) {
        String secretKey = encryptDecryptGenerator.decryptData(exchangeInfo.getSecretKey());
        String apiKey = encryptDecryptGenerator.decryptData(exchangeInfo.getApiKey());
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
                data.getData(), exchangeInfo.getExchange());
    }

    @SneakyThrows
    @Override
    public List<AssetCandleDto> getPeriodAssetPriceCandles(PeriodAssetPriceCandlesRequest request,
                                                           UserExchangeInfo exchangeInfo) {
        String secretKey = encryptDecryptGenerator.decryptData(exchangeInfo.getSecretKey());
        String apiKey = encryptDecryptGenerator.decryptData(exchangeInfo.getApiKey());
        String parametersString = this.getAssetPriceCandlesParamsString(request, secretKey);
        String requestUrl = this.getRequestUrl(CANDLE_ASSET_PRICE_PATH, parametersString);
        HttpHeaders httpHeaders = this.addHttpHeaders(BINGX_API_KEY_NAME, apiKey);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        log.info("[TRADING BOT] Time: {} | Market-data-service | getPeriodAssetPriceCandles" +
                        " | asset's params : {} | action: {}",
                Timestamp.from(Instant.now()), request, "get period asset price candles");
        return Objects.requireNonNull(restTemplate.exchange(
                        requestUrl,
                        HttpMethod.GET,
                        entity,
                        AssetPriceDataDto.class).getBody()).getData().stream()
                .peek(asset -> asset.setSymbol(request.getSymbol())).collect(Collectors.toList());
    }

    @SneakyThrows
    private String getAssetPriceCandlesParamsString(PeriodAssetPriceCandlesRequest request, String secretKey) {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put(TIMESTAMP, "" + new Timestamp(System.currentTimeMillis()).getTime());
        parameters.put(SYMBOL, request.getSymbol());
        parameters.put(INTERVAL, request.getInterval());
        parameters.put(START_TIME, this.convertToMillisecs(request.getStartTime()) + "");
        parameters.put(END_TIME, this.convertToMillisecs(request.getEndTime()) + "");
        parameters.put(LIMIT, String.valueOf(request.getLimit()));
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
