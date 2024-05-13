package monaco.bot.marketdata.mapper;

import monaco.bot.marketdata.dto.AssetCandleDto;
import monaco.bot.marketdata.dto.binance.CandleStickDataDto;
import org.springframework.stereotype.Component;

@Component
public class AssetCandleConverter {

    public AssetCandleDto convertToAssetCandleDto(CandleStickDataDto data){
        return AssetCandleDto.builder()
                .volume(data.getVolume())
                .low(data.getLowPrice())
                .high(data.getHighPrice())
                .open(data.getOpenPrice())
                .close(data.getClosePrice())
                .time(data.getOpenTime())
                .build();
    }

}
