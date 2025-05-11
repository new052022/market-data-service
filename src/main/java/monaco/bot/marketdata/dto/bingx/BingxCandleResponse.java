package monaco.bot.marketdata.dto.bingx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BingxCandleResponse {

    private int code;
    private String msg;
    private BingxCandleDataWrapper[] data;

}
