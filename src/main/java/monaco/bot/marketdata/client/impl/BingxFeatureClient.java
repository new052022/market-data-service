package monaco.bot.marketdata.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import monaco.bot.marketdata.dto.AssetPriceDto;
import monaco.bot.marketdata.model.UserExchangeInfo;
import monaco.bot.marketdata.service.interfaces.UserExchangeInfoService;
import monaco.bot.marketdata.util.EncryptDecryptGenerator;
import monaco.bot.marketdata.util.SignatureGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

import static monaco.bot.marketdata.util.Constants.BINGX_API_KEY_NAME;
import static monaco.bot.marketdata.util.Constants.SYMBOL;
import static monaco.bot.marketdata.util.Constants.TIMESTAMP;

@Slf4j
@Component
@RequiredArgsConstructor
public class BingxFeatureClient {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final EncryptDecryptGenerator encryptDecryptGenerator;

    private final UserExchangeInfoService exchangeInfoService;

    private static final String ASSET_PRICE_PATH = "/openApi/swap/v1/ticker/price";

    @Value("${exchange-url.bingx-perpetual}")
    private String url;

    @SneakyThrows
    public AssetPriceDto getAssetPrice(String symbol, Long userId, String exchange) {
        UserExchangeInfo exchangeInfo = exchangeInfoService.getExchangeInfoByNameAndUserId(userId,exchange);
        String secretKey = encryptDecryptGenerator.decryptData(exchangeInfo.getSecretKey());
        String apiKey = encryptDecryptGenerator.decryptData(exchangeInfo.getApiKey());
        String parametersString = this.getParamsString(symbol, secretKey);
        String requestUrl = this.getRequestUrl(ASSET_PRICE_PATH, parametersString);
        HttpHeaders httpHeaders = this.addHttpHeaders(BINGX_API_KEY_NAME, apiKey);
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        log.info("[TRADING BOT] Time: {} | Market-data-service | get asset price" +
                        " | asset's name : {} | action: {}",
                Timestamp.from(Instant.now()), symbol, "get asset price");
        return restTemplate.exchange(
                requestUrl,
                HttpMethod.GET,
                entity,
                AssetPriceDto.class).getBody();
    }

    @SneakyThrows
    private String getParamsString(String symbol, String secretKey) {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put(TIMESTAMP, "" + new Timestamp(System.currentTimeMillis()).getTime());
        parameters.put(SYMBOL, symbol);
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

    private HttpHeaders addHttpHeaders(String apiName, String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(apiName, apiKey);
        return headers;
    }

}
