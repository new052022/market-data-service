package monaco.bot.marketdata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import monaco.bot.marketdata.model.Exchange;
import monaco.bot.marketdata.model.FilterType;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetContractResponseDto {

    private Long id;

    private String symbol;

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

    private String binanceContractType;

    private ExchangeResponseDto exchange;

    private List<FilterTypeResonseDto> filters;

}
