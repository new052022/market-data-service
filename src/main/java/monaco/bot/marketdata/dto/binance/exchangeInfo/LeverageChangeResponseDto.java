package monaco.bot.marketdata.dto.binance.exchangeInfo;

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
public class LeverageChangeResponseDto {

    private Long leverage;

    private String maxNotionalValue;

    private String symbol;

}
