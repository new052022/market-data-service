package monaco.bot.marketdata.client.interfaces;

import monaco.bot.marketdata.dto.AssetContractDataDto;
import monaco.bot.marketdata.dto.AssetPriceDataDto;
import monaco.bot.marketdata.dto.PeriodAssetPriceCandlesRequest;
import monaco.bot.marketdata.dto.SingleAssetPriceDto;
import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.model.UserExchangeInfo;

import java.util.List;

public interface MarketDataClient {

    SingleAssetPriceDto getAssetPrice(String symbol, UserExchangeInfo exchangeInfo);

    AssetPriceDataDto getPeriodAssetPriceCandles(PeriodAssetPriceCandlesRequest request,
                                                 UserExchangeInfo exchangeInfo);

    List<AssetContract> getAssetDetails(UserExchangeInfo exchangeInfo);

}
