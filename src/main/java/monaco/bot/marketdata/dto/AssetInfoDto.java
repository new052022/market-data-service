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
public class AssetInfoDto {

    private String asset;
    private BigDecimal walletBalance;
    private BigDecimal unrealizedProfit;
    private BigDecimal marginBalance;
    private BigDecimal maintMargin;
    private BigDecimal initialMargin;
    private BigDecimal positionInitialMargin;
    private BigDecimal openOrderInitialMargin;
    private BigDecimal crossWalletBalance;
    private BigDecimal crossUnPnl;
    private BigDecimal availableBalance;
    private BigDecimal maxWithdrawAmount;
    private Long updateTime;

}
