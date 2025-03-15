package monaco.bot.marketdata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import monaco.bot.marketdata.dto.AssetCandleDto;
import monaco.bot.marketdata.dto.AssetContractResponseDto;
import monaco.bot.marketdata.dto.AssetPriceDto;
import monaco.bot.marketdata.dto.ChangeLeverageDto;
import monaco.bot.marketdata.dto.LeverageSizeDto;
import monaco.bot.marketdata.dto.PeriodAssetPriceCandlesRequest;
import monaco.bot.marketdata.dto.SymbolConfigDto;
import monaco.bot.marketdata.dto.SymbolRequestDto;
import monaco.bot.marketdata.dto.SymbolResponseDto;
import monaco.bot.marketdata.service.impl.ExchangesIntegrationService;
import monaco.bot.marketdata.service.interfaces.AssetContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/asset-price")
@Tag(name = "Asset-price controller")
public class AssetPriceController {

    private final ExchangesIntegrationService exchangesIntegrationService;

    private final AssetContractService assetContractService;

    @SneakyThrows
    @GetMapping("{userId}")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Get asset price")
    public ResponseEntity<List<AssetPriceDto>> getAssetsPrices(@PathVariable Long userId, String exchange) {
            return ResponseEntity.ok(exchangesIntegrationService.getAssetsPrices(userId,exchange));
    }

    @SneakyThrows
    @GetMapping("candles/{userId}")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Get period asset's candle price")
    public ResponseEntity<List<AssetCandleDto>> getAssetPriceCandles(@PathVariable Long userId,
                                                                     PeriodAssetPriceCandlesRequest request) {
        return ResponseEntity.ok(exchangesIntegrationService.getCandlesByInterval(userId, request));
    }

    @SneakyThrows
    @GetMapping("asset-details")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Get asset details")
    public ResponseEntity<List<AssetContractResponseDto>> getAssetDetails(String exchange) {
        return ResponseEntity.ok(assetContractService.getByExchange(exchange));
    }

    @SneakyThrows
    @GetMapping("/{userId}/symbol-leverage")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Get symbol leverage")
    public ResponseEntity<LeverageSizeDto> getSymbolLeverage(@PathVariable Long userId, String exchange,
                                                             String symbol) {
        return ResponseEntity.ok(exchangesIntegrationService.getSymbolLeverage(userId, exchange ,symbol));
    }

    @SneakyThrows
    @GetMapping("/{userId}/symbol-config")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Get user symbol config")
    public ResponseEntity<List<SymbolConfigDto>> getUserSymbolConfig(@PathVariable Long userId, String exchange,
                                                                     String symbol) {
        return ResponseEntity.ok(exchangesIntegrationService.getSymbolConfig(userId, exchange ,symbol));
    }

    /**
     * @param userId   user id
     * @param exchange Bingx/Binance
     * @param symbol   ex - BTC-USDT
     * @param leverage number
     * @param side     LONG/SHORT
     * @return
     */
    @SneakyThrows
    @PostMapping("/{userId}/symbol-leverage")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Change symbol leverage")
    public ResponseEntity<ChangeLeverageDto> changeSymbolLeverage(@PathVariable Long userId, String exchange,
                                                                  String symbol, Long leverage, String side) {
        return ResponseEntity.ok(exchangesIntegrationService.updateSymbolLeverage(userId, exchange, symbol, leverage, side));
    }

    @SneakyThrows
    @GetMapping("symbol-data")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Get symbols' data")
    public ResponseEntity<SymbolResponseDto> getSymbolData(SymbolRequestDto requestDto) {
        return ResponseEntity.ok(assetContractService.getSymbolsByParams(requestDto));
    }

    /** update asset contracts
     */
    @SneakyThrows
    @PostMapping("/asset-contracts")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Updatr asset-contracts")
    public ResponseEntity<HttpStatus> updateAssetContracts() {
        assetContractService.updateAssetContracts();
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
