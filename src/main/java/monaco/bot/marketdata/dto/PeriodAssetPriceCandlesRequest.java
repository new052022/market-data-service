package monaco.bot.marketdata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeriodAssetPriceCandlesRequest {

    private List<String> symbols;

    private String interval;

    private Long limit;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String exchange;

    @Override
    public String toString() {
        return new StringJoiner(", ", PeriodAssetPriceCandlesRequest.class.getSimpleName() + "[", "]")
                .add("symbols=" + symbols)
                .add("interval='" + interval + "'")
                .add("limit=" + limit)
                .add("startTime=" + startTime)
                .add("endTime=" + endTime)
                .add("exchange='" + exchange + "'")
                .toString();
    }

}
