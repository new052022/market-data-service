package monaco.bot.marketdata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import monaco.bot.marketdata.client.impl.BingxFeatureClient;
import monaco.bot.marketdata.dto.AssetPriceDataDto;
import monaco.bot.marketdata.dto.AssetPriceDto;
import monaco.bot.marketdata.dto.PeriodAssetPriceCandlesRequest;
import monaco.bot.marketdata.dto.SingleAssetPriceDto;
import monaco.bot.marketdata.util.EncryptDecryptGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/asset-price")
@Tag(name = "Asset-price controller")
public class AssetPriceController {

    private final BingxFeatureClient bingxFeatureClient;

    @SneakyThrows
    @GetMapping("{userId}")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Get asset price")
    public ResponseEntity<SingleAssetPriceDto> getAssetPrice(@PathVariable Long userId, String symbol, String exchange) {
        return ResponseEntity.ok(bingxFeatureClient.getAssetPrice(symbol,userId,exchange));
    }

    @SneakyThrows
    @GetMapping("candles/{userId}")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Get period asset's candle price")
    public ResponseEntity<AssetPriceDataDto> getAssetPriceCandles(@PathVariable Long userId, String exchange,
                                                         PeriodAssetPriceCandlesRequest request) {
        return ResponseEntity.ok(bingxFeatureClient.getPeriodAssetPriceCandles(request,userId,exchange));
    }

}
