package monaco.bot.marketdata.client.interfaces;

import monaco.bot.marketdata.dto.AssetCandleDto;
import monaco.bot.marketdata.dto.AssetPriceDto;
import monaco.bot.marketdata.dto.ChangeLeverageDto;
import monaco.bot.marketdata.dto.LeverageSizeDto;
import monaco.bot.marketdata.dto.PeriodAssetPriceCandlesRequest;
import monaco.bot.marketdata.model.AssetContract;

import java.util.List;

public interface MarketDataClient {

    List<AssetPriceDto> getAssetsPrices(String apiKey, String secretKey);

    List<AssetCandleDto> getPeriodAssetPriceCandles(PeriodAssetPriceCandlesRequest request,
                                                    String apiKey, String secretKey, String exchange);

    List<AssetContract> getAssetDetails(String apiKey,String secretKey, String exchange);

    LeverageSizeDto getSymbolLeverage(String symbol, String apiKey, String secretKey, String exchange);

    ChangeLeverageDto updateSymbolLeverage(String symbol, Long leverage,
                                           String side, String apiKey, String secretKey);

}
