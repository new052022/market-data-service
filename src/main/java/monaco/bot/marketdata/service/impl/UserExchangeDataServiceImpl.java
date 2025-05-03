package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import monaco.bot.marketdata.client.interfaces.BinancePersonalDataClient;
import monaco.bot.marketdata.dto.UserExchangeResponseDto;
import monaco.bot.marketdata.dto.binance.user_trades.UserTradesHistoryResponseDto;
import monaco.bot.marketdata.service.interfaces.UserExchangeDataService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserExchangeDataServiceImpl implements UserExchangeDataService {

    private final Map<String, BinancePersonalDataClient> personalDataClientMap;

    private final UsersService usersService;

    @Override
    public List<UserTradesHistoryResponseDto> getUsersTradeHistory(Long userId, String exchange) {
        if (Objects.isNull(exchange)) {
            List<UserExchangeResponseDto> userExchangesList = usersService.getUsersExchanges(userId);
            return userExchangesList.stream()
                    .flatMap(userExchange -> this.fetchUserTradesForExchange(
                            userExchange.getExchangeName(),
                            userExchange.getApiKey(),
                            userExchange.getSecretKey()).stream())
                    .toList();
        } else {
            UserExchangeResponseDto userExchangeInfo = usersService.getUserExchangeInfoByUserId(userId, exchange);
            return this.fetchUserTradesForExchange(
                    exchange,
                    userExchangeInfo.getApiKey(),
                    userExchangeInfo.getSecretKey());
        }
    }

    private List<UserTradesHistoryResponseDto> fetchUserTradesForExchange(
            String exchangeName,
            String apiKey,
            String secretKey) {
        String clientName = exchangeName + "-user";
        BinancePersonalDataClient exchangeClient = personalDataClientMap.get(clientName);
        if (exchangeClient == null) {
            throw new IllegalArgumentException("Exchange client not found for: " + exchangeName);
        }
        return exchangeClient.getUserTradesHistory(apiKey, secretKey).stream()
                .peek(userTrades -> userTrades.setExchange(exchangeName))
                .toList();
    }

}
