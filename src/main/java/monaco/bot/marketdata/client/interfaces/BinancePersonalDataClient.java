package monaco.bot.marketdata.client.interfaces;

import monaco.bot.marketdata.dto.AccountInformationDto;
import monaco.bot.marketdata.dto.binance.user_trades.UserTradesHistoryResponseDto;

import java.util.List;

public interface BinancePersonalDataClient {

    AccountInformationDto getAccountBalances(String encodedApiKey, String encodedSecretKey);

    List<UserTradesHistoryResponseDto> getUserTradesHistory(String encodedApiKey, String encodedSecretKey);

}
