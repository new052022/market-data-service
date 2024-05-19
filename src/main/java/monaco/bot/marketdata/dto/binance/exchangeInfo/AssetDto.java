package monaco.bot.marketdata.dto.binance.exchangeInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetDto {
    private String asset;
    private boolean marginAvailable;
    private String autoAssetExchange;
}
