package monaco.bot.marketdata.dto.binance.exchangeInfo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssetInfoDto {
    private String timezone;
    private long serverTime;
    private String futuresType;
    private List<RateLimitDto> rateLimits;
    private List<String> exchangeFilters;
    private List<AssetDto> assets;
    private List<SymbolDto> symbols;
}
