package monaco.bot.marketdata.dto.bingx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BingxCandleData {

    private Double open;
    private Double close;
    private Double high;
    private Double low;
    private Double volume;
    private Long time;

}
