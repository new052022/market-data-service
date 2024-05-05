package monaco.bot.marketdata.client.interfaces;

import monaco.bot.marketdata.dto.AssetPriceDataDto;
import monaco.bot.marketdata.dto.AssetPriceDto;

public interface MarketDataClient {

    AssetPriceDto getAssetPrice();

    AssetPriceDataDto getCandleAssetPrices();

}
