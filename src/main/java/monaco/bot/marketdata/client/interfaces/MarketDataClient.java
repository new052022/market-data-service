package monaco.bot.marketdata.client.interfaces;

import monaco.bot.marketdata.dto.AssetCandleDto;
import monaco.bot.marketdata.dto.AssetContractDataDto;
import monaco.bot.marketdata.dto.AssetPriceDataDto;
import monaco.bot.marketdata.dto.AssetPriceDto;
import monaco.bot.marketdata.dto.PeriodAssetPriceCandlesRequest;
import monaco.bot.marketdata.dto.SingleAssetPriceDto;
import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.model.UserExchangeInfo;

import java.util.List;

public interface MarketDataClient {

    AssetPriceDto getAssetPrice(String symbol, UserExchangeInfo exchangeInfo);

    List<AssetCandleDto> getPeriodAssetPriceCandles(PeriodAssetPriceCandlesRequest request,
                                                    UserExchangeInfo exchangeInfo);

    List<AssetContract> getAssetDetails(UserExchangeInfo exchangeInfo);

}
