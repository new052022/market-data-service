package monaco.bot.marketdata.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import monaco.bot.marketdata.client.interfaces.BinancePersonalDataClient;
import monaco.bot.marketdata.dto.AccountInformationDto;
import monaco.bot.marketdata.dto.binance.user_trades.UserTradesHistoryResponseDto;
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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static monaco.bot.marketdata.util.Constants.BINANCE_API_KEY_NAME;
import static monaco.bot.marketdata.util.Constants.RECV_WINDOW;
import static monaco.bot.marketdata.util.Constants.TIMESTAMP;

@Slf4j
@Service("Binance-user")
@RequiredArgsConstructor
public class BinancePersonalDataClientImpl implements BinancePersonalDataClient {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final EncryptDecryptGenerator encryptDecryptGenerator;

    @Value("${exchange-url.binance-perpetual.v1}")
    private String url;

    public static final String USER_TRADES_HISTORY = "/userTrades";

    private final static String GENERAL_BINANCE_API = "https://fapi.binance.com";

    public static final String BALANCE = "/fapi/v3/account";

    @Override
    @SneakyThrows
    public AccountInformationDto getAccountBalances(String encodedSecretKey, String encodedApiKey) {
        String time = "" + new Timestamp(System.currentTimeMillis()).getTime();
        String recvWindows = "15000";
        String secretKey = encryptDecryptGenerator.decryptData(encodedSecretKey);
        String apiKey = encryptDecryptGenerator.decryptData(encodedApiKey);
        String params = this.getEncryptedParams(secretKey, time, recvWindows);
        String requestUrl = this.getAccountUrl(BALANCE, params);
        HttpHeaders headers = this.addHttpHeaders(BINANCE_API_KEY_NAME, apiKey);
        String responseBody = restTemplate.exchange(
                requestUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        AccountInformationDto accountInformation = objectMapper.readValue(responseBody, AccountInformationDto.class);
        log.info("[TRADING BOT] Time: {} | Order-service | getBalances" +
                        " | number of assets in balance : {} | action: {}",
                Timestamp.from(Instant.now()), accountInformation.getAvailableBalance(), "fetch account balances");
        return accountInformation;
    }

    @SneakyThrows
    public List<UserTradesHistoryResponseDto> getUserTradesHistory(String encodedApiKey, String encodedSecretKey) {
        String time = "" + new Timestamp(System.currentTimeMillis()).getTime();
        String recvWindows = "15000";
        String secretKey = encryptDecryptGenerator.decryptData(encodedSecretKey);
        String apiKey = encryptDecryptGenerator.decryptData(encodedApiKey);
        String params = this.getEncryptedParams(secretKey, time, recvWindows);
        String requestUrl = this.getRequestUrl(USER_TRADES_HISTORY, params);
        HttpHeaders headers = this.addHttpHeaders(BINANCE_API_KEY_NAME, apiKey);
        String tradeHistoryResponse = restTemplate.exchange(
                requestUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
        List<UserTradesHistoryResponseDto> response = objectMapper.readValue(tradeHistoryResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserTradesHistoryResponseDto.class));
        log.info("[TRADING BOT] Time: {} | Market-data-service | getUserTradesHistory" +
                        " | user's trade history : {} | action: {}",
                Timestamp.from(Instant.now()), response.size(), "get user's trade history");
        return response;
    }

    @SneakyThrows
    private String getEncryptedParams(String secretKey, String time, String recvWindow) {
        TreeMap<String, String> parameters = new TreeMap<>();
        parameters.put(TIMESTAMP, time);
        parameters.put(RECV_WINDOW, recvWindow);
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
        return url + path + "?" + parameters;
    }

    private String getAccountUrl(String path, String parameters) {
        return GENERAL_BINANCE_API + path + "?" + parameters;
    }

    private HttpHeaders addHttpHeaders(String apiName, String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(apiName, apiKey);
        return headers;
    }

}
