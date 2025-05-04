package monaco.bot.marketdata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionDto {

    private String symbol;
    private String positionSide;
    private BigDecimal positionAmt;
    private BigDecimal unrealizedProfit;
    private BigDecimal isolatedMargin;
    private BigDecimal notional;
    private BigDecimal isolatedWallet;
    private BigDecimal initialMargin;
    private BigDecimal maintMargin;
    private Long updateTime;

}
