package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import monaco.bot.marketdata.client.interfaces.BinancePersonalDataClient;
import monaco.bot.marketdata.dto.UserExchangeResponseDto;
import monaco.bot.marketdata.dto.binance.user_trades.UserTradesHistoryResponseDto;
import monaco.bot.marketdata.service.interfaces.UserExchangeDataService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserExchangeDataServiceImpl implements UserExchangeDataService {

    private final Map<String, BinancePersonalDataClient> personalDataClientMap;

    private final UsersService usersService;

    @Override
    public List<UserTradesHistoryResponseDto> getUsersTradeHistory(Long userId, String exchange) {
        UserExchangeResponseDto userExchangeInfo = usersService.getUserExchangeInfoByUserId(userId, exchange);
        String exchangeClientName = exchange + "-user";
        BinancePersonalDataClient exchangeClient = personalDataClientMap.get(exchangeClientName);
        return exchangeClient.getUserTradesHistory(
                userExchangeInfo.getApiKey(), userExchangeInfo.getSecretKey());
    }

}
