package monaco.bot.marketdata.dto.bingx;

import lombok.Data;
import monaco.bot.marketdata.dto.AssetPriceDto;

import java.util.List;

@Data
public class BingxApiResponse {

    private int code;

    private String msg;

    private List<AssetPriceDto> data;

}
