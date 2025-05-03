package monaco.bot.marketdata.client.interfaces;

import monaco.bot.marketdata.dto.binance.user_trades.UserTradesHistoryResponseDto;

import java.util.List;

public interface BinancePersonalDataClient {

    List<UserTradesHistoryResponseDto> getUserTradesHistory(String encodedApiKey, String encodedSecretKey);

}
