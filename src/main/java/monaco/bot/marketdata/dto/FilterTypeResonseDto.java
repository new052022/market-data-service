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
public class FilterTypeResonseDto {

    private Long id;

    private String FilterType;

    private Double maxPrice;

    private Double minPrice;

    private Double tickSize;

    private Double maxQty;

    private Double minQty;

    private Double stepSize;

    private Double limit;

    private Double notional;

    private Double multiplierUp;

    private Double multiplierDown;

    private Double multiplierDecimal;

}
