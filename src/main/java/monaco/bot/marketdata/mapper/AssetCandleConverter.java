package monaco.bot.marketdata.mapper;

import monaco.bot.marketdata.dto.AssetCandleDto;
import monaco.bot.marketdata.dto.binance.CandleStickDataDto;
import monaco.bot.marketdata.dto.binance.LeverageDto;
import monaco.bot.marketdata.dto.binance.exchangeInfo.SymbolDto;
import monaco.bot.marketdata.model.AssetContract;
import org.springframework.stereotype.Component;

@Component
public class AssetCandleConverter {

    public AssetCandleDto convertToAssetCandleDto(CandleStickDataDto data){
        return AssetCandleDto.builder()
                .volume(data.getQuoteAssetVolume())
                .low(data.getLowPrice())
                .high(data.getHighPrice())
                .open(data.getOpenPrice())
                .close(data.getClosePrice())
                .time(data.getOpenTime())
                .build();
    }

}
