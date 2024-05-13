package monaco.bot.marketdata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetDetailsDto {

    private String contractId;

    private String symbol;

    private String size;

    private Double quantityPrecision;

    private Double pricePrecision;

    private Double feeRate;

    private Double makerFeeRate;

    private Double takerFeeRate;

    private Double tradeMinLimit;

    private Double tradeMinQuantity;

    private Double tradeMinUSDT;

    private Long maxLongLeverage;

    private Long maxShortLeverage;

    private String currency;

    private String asset;

    private Long status;

    private String apiStateOpen;

    private String apiStateClose;

}
