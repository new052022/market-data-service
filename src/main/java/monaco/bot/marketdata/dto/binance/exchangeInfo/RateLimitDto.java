package monaco.bot.marketdata.dto.binance.exchangeInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateLimitDto {
    private String rateLimitType;
    private String interval;
    private int intervalNum;
    private int limit;
}
