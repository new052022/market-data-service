package monaco.bot.marketdata.dto.binance.exchangeInfo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SymbolDto {
    private String symbol;
    private String pair;
    private String contractType;
    private long deliveryDate;
    private long onboardDate;
    private String status;
    private String maintMarginPercent;
    private String requiredMarginPercent;
    private String baseAsset;
    private String quoteAsset;
    private String marginAsset;
    private int pricePrecision;
    private int quantityPrecision;
    private int baseAssetPrecision;
    private int quotePrecision;
    private String underlyingType;
    private List<String> underlyingSubType;
    private int settlePlan;
    private String triggerProtect;
    private String liquidationFee;
    private String marketTakeBound;
    private int maxMoveOrderLimit;
    private List<FilterDto> filters;
    private List<String> orderTypes;
    private List<String> timeInForce;
}
