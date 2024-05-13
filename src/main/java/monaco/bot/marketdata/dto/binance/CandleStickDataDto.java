package monaco.bot.marketdata.dto.binance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class CandleStickDataDto {

    private Timestamp openTime;

    private Double openPrice;

    private Double highPrice;

    private Double lowPrice;

    private Double closePrice;

    private Double volume;

    private Timestamp closeTime;

    private String quoteAssetVolume;

    private Long numberOfTrades;

    private Double takerBuyBaseAssetVolume;

    private Double takerBuyQuoteAssetVolume;

    private String ignore;

    private String symbol;

    @JsonCreator
    public CandleStickDataDto(
            @JsonProperty("open_time") Timestamp openTime,
            @JsonProperty("open_price") Double openPrice,
            @JsonProperty("high_price") Double highPrice,
            @JsonProperty("low_price") Double lowPrice,
            @JsonProperty("close_price") Double closePrice,
            @JsonProperty("volume") Double volume,
            @JsonProperty("close_time") Timestamp closeTime,
            @JsonProperty("quote_asset_volume") String quoteAssetVolume,
            @JsonProperty("number_of_trades") Long numberOfTrades,
            @JsonProperty("taker_buy_base_asset_volume") Double takerBuyBaseAssetVolume,
            @JsonProperty("taker_buy_quote_asset_volume") Double takerBuyQuoteAssetVolume,
            @JsonProperty("ignore") String ignore
    ) {
        this.openTime = openTime;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.closeTime = closeTime;
        this.quoteAssetVolume = quoteAssetVolume;
        this.numberOfTrades = numberOfTrades;
        this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
        this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
        this.ignore = ignore;
    }
}
