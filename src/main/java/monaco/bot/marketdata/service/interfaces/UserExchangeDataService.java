package monaco.bot.marketdata.service.interfaces;

import monaco.bot.marketdata.dto.AccountInformationDto;
import monaco.bot.marketdata.dto.binance.user_trades.UserTradesHistoryResponseDto;

import java.util.List;

public interface UserExchangeDataService {

    List<AccountInformationDto> getUserAccountInfo(Long userId, String exchange);

    List<UserTradesHistoryResponseDto> getUsersTradeHistory(Long userId, String exchange);
}
