package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import monaco.bot.marketdata.client.interfaces.MarketDataClient;
import monaco.bot.marketdata.dto.AssetCandleDto;
import monaco.bot.marketdata.dto.AssetPriceDto;
import monaco.bot.marketdata.dto.ChangeLeverageDto;
import monaco.bot.marketdata.dto.ChangeMarginTypeDto;
import monaco.bot.marketdata.dto.LeverageSizeDto;
import monaco.bot.marketdata.dto.PeriodAssetPriceCandlesRequest;
import monaco.bot.marketdata.dto.SymbolConfigDto;
import monaco.bot.marketdata.dto.UserExchangeResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangesIntegrationService {

    private final Map<String, MarketDataClient> marketDataClients;

    private final UsersService usersService;

    public List<AssetPriceDto> getAssetsPrices(Long userId, String exchange) {
        UserExchangeResponseDto userInfo = usersService.getUserExchangeInfoByUserId(userId, exchange);
        return marketDataClients.get(exchange).getAssetsPrices(userInfo.getApiKey(), userInfo.getSecretKey());
    }

    public List<AssetCandleDto> getCandlesByInterval(Long userId, PeriodAssetPriceCandlesRequest request) {
        String exchange = request.getExchange();
        log.info("Input params for getting candles prices: {}", request);
        UserExchangeResponseDto userInfo = usersService.getUserExchangeInfoByUserId(userId,exchange);
       return marketDataClients.get(exchange).getPeriodAssetPriceCandles(request, userInfo.getApiKey(),
               userInfo.getSecretKey(), exchange);
    }

    public LeverageSizeDto getSymbolLeverage(Long userId, String exchange, String symbol) {
        UserExchangeResponseDto userInfo = usersService.getUserExchangeInfoByUserId(userId, exchange);
        return marketDataClients.get(exchange).getSymbolLeverage(symbol, userInfo.getApiKey(), userInfo.getSecretKey(), exchange);
    }

    public ChangeLeverageDto updateSymbolLeverage(Long userId, String exchange, String symbol, Long leverage, String side) {
        UserExchangeResponseDto userInfo = usersService.getUserExchangeInfoByUserId(userId, exchange);
        log.info("Leverage of {} will be updated for user {} with exchange {}",
                symbol, userInfo.getUserId(), userInfo.getExchangeName());
       return marketDataClients.get(exchange).updateSymbolLeverage(symbol, leverage,
               side, userInfo.getApiKey(), userInfo.getSecretKey());
    }

    public  List<SymbolConfigDto> getSymbolConfig(Long userId, String exchange, String symbol) {
        UserExchangeResponseDto userInfo = usersService.getUserExchangeInfoByUserId(userId, exchange);
        return marketDataClients.get(exchange).getSymbolConfig(symbol, userInfo.getApiKey(), userInfo.getSecretKey(), exchange);
    }

    public List<ChangeLeverageDto> updateAllSymbolsLeverage(Long userId, String exchange, Long leverage, String side) {
        UserExchangeResponseDto userInfo = usersService.getUserExchangeInfoByUserId(userId, exchange);
        log.info("Leverage will be updated for all symbols for user {} with exchange {}", userInfo.getUserId(), userInfo.getExchangeName());

        // Get all asset prices to extract symbols
        List<AssetPriceDto> assetPrices = marketDataClients.get(exchange).getAssetsPrices(userInfo.getApiKey(), userInfo.getSecretKey());

        // Update leverage for each symbol
        return assetPrices.stream()
                .map(asset -> {
                    try {
                        return marketDataClients.get(exchange).updateSymbolLeverage(
                                asset.getSymbol(),
                                leverage,
                                side,
                                userInfo.getApiKey(),
                                userInfo.getSecretKey()
                        );
                    } catch (Exception e) {
                        log.error("Failed to update leverage for symbol {}: {}", asset.getSymbol(), e.getMessage());
                        return ChangeLeverageDto.builder()
                                .symbol(asset.getSymbol())
                                .leverage(null)
                                .build();
                    }
                })
                .toList();
    }

    public List<ChangeMarginTypeDto> updateAllSymbolsMarginType(Long userId, String exchange, String marginType) {
        UserExchangeResponseDto userInfo = usersService.getUserExchangeInfoByUserId(userId, exchange);
        log.info("Margin type will be updated to {} for all symbols for user {} with exchange {}",
                marginType, userInfo.getUserId(), userInfo.getExchangeName());

        // Get all asset prices to extract symbols
        List<AssetPriceDto> assetPrices = marketDataClients.get(exchange).getAssetsPrices(userInfo.getApiKey(), userInfo.getSecretKey());

        // Update margin type for each symbol
        return assetPrices.stream()
                .map(asset -> {
                    try {
                        return marketDataClients.get(exchange).updateMarginType(
                                asset.getSymbol(),
                                marginType,
                                userInfo.getApiKey(),
                                userInfo.getSecretKey()
                        );
                    } catch (Exception e) {
                        log.error("Failed to update margin type for symbol {}: {}", asset.getSymbol(), e.getMessage());
                        return ChangeMarginTypeDto.builder()
                                .symbol(asset.getSymbol())
                                .marginType(null)
                                .build();
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
