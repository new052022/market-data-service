package monaco.bot.marketdata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountInformationDto {

    private BigDecimal totalInitialMargin;
    private BigDecimal totalMaintMargin;
    private BigDecimal totalWalletBalance;
    private BigDecimal totalUnrealizedProfit;
    private BigDecimal totalMarginBalance;
    private BigDecimal totalPositionInitialMargin;
    private BigDecimal totalOpenOrderInitialMargin;
    private BigDecimal totalCrossWalletBalance;
    private BigDecimal totalCrossUnPnl;
    private BigDecimal availableBalance;
    private BigDecimal maxWithdrawAmount;

    private List<AssetInfoDto> assets;
    private List<PositionDto> positions;
    private String exchange;

}
