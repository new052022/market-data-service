package monaco.bot.marketdata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SymbolRequestDto {

    private Long userId;

    private Long volume;

    private Long leverage;

    private List<String> exchanges;

    private String interval;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long limit;

    private String contractType;

}
