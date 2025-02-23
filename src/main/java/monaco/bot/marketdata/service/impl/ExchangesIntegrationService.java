package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import monaco.bot.marketdata.client.interfaces.MarketDataClient;
import monaco.bot.marketdata.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExchangesIntegrationService {

    private final Map<String, MarketDataClient> marketDataClients;

    private final UsersService usersService;

    public List<AssetPriceDto> getAssetsPrices(Long userId, String exchange) {
        UserExchangeResponseDto userInfo = usersService.getUserExchangeInfoByUserId(userId, exchange);
        return marketDataClients.get(exchange).getAssetsPrices(userInfo.getApiKey(), userInfo.getSecretKey());
    }

    public List<AssetCandleDto> getCandlesByInterval(Long userId, String exchange, PeriodAssetPriceCandlesRequest request) {
        UserExchangeResponseDto userInfo = usersService.getUserExchangeInfoByUserId(userId, exchange);
       return marketDataClients.get(exchange).getPeriodAssetPriceCandles(request, userInfo.getApiKey(),
               userInfo.getSecretKey(), exchange);
    }

    public LeverageSizeDto getSymbolLeverage(Long userId, String exchange, String symbol) {
        UserExchangeResponseDto userInfo = usersService.getUserExchangeInfoByUserId(userId, exchange);
        return marketDataClients.get(exchange).getSymbolLeverage(symbol, userInfo.getApiKey(), userInfo.getSecretKey(), exchange);
    }

    public ChangeLeverageDto updateSymbolLeverage(Long userId, String exchange, String symbol, Long leverage, String side) {
        UserExchangeResponseDto userInfo = usersService.getUserExchangeInfoByUserId(userId, exchange);
       return marketDataClients.get(exchange).updateSymbolLeverage(symbol, leverage, side, userInfo.getApiKey(), userInfo.getSecretKey());
    }
}
