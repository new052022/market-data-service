package monaco.bot.marketdata.service.interfaces;

import monaco.bot.marketdata.dto.binance.user_trades.UserTradesHistoryResponseDto;

import java.util.List;

public interface UserExchangeDataService {
    List<UserTradesHistoryResponseDto> getUsersTradeHistory(Long userId, String exchange);
}
