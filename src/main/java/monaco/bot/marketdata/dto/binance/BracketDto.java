package monaco.bot.marketdata.dto.binance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BracketDto {

    private Integer bracket;

    private Integer initialLeverage;

    private Double notionalCap;

    private Double notionalFloor;

    private Double maintMarginRatio;

    private Double cum;

}
