package monaco.bot.marketdata.dto.binance;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CandleStickPriceDto {

    private Timestamp openTime;

    private Double openPrice;

    private Double highPrice;

    private Double lowPrice;

    private Double closePrice;

    private Timestamp closeTime;

    private String symbol;

    private Double volume;

    private String quoteAssetVolume;

    private Long numberOfTrades;

    private String interval;

}
