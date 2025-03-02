package monaco.bot.marketdata.dto;

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
public class SymbolConfigDto {

    private String symbol;

    private String marginType;

    private Boolean isAutoAddMargin;

    private Integer leverage;

    private String maxNotionalValue;

}
