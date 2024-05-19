package monaco.bot.marketdata.dto.binance.exchangeInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterDto {
    private String maxPrice;
    private String tickSize;
    private String minPrice;
    private String filterType;
    private String maxQty;
    private String stepSize;
    private String minQty;
    private int limit;
    private String notional;
    private String multiplierDecimal;
    private String multiplierDown;
    private String multiplierUp;

}
