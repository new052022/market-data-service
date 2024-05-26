package monaco.bot.marketdata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import monaco.bot.marketdata.client.interfaces.MarketDataClient;
import monaco.bot.marketdata.dto.AssetCandleDto;
import monaco.bot.marketdata.dto.AssetContractResponseDto;
import monaco.bot.marketdata.dto.AssetPriceDto;
import monaco.bot.marketdata.dto.ChangeLeverageDto;
import monaco.bot.marketdata.dto.LeverageSizeDto;
import monaco.bot.marketdata.dto.PeriodAssetPriceCandlesRequest;
import monaco.bot.marketdata.dto.SymbolParamsDto;
import monaco.bot.marketdata.dto.SymbolRequestDto;
import monaco.bot.marketdata.dto.SymbolResponseDto;
import monaco.bot.marketdata.mapper.AssetContractMapper;
import monaco.bot.marketdata.model.UserExchangeInfo;
import monaco.bot.marketdata.service.interfaces.AssetContractService;
import monaco.bot.marketdata.service.interfaces.UserExchangeInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/asset-price")
@Tag(name = "Asset-price controller")
public class AssetPriceController {

    private final Map<String, MarketDataClient> featureClients;

    private final AssetContractService assetContractService;

    private final UserExchangeInfoService exchangeInfoService;

    private final AssetContractMapper assetContractMapper;

    @SneakyThrows
    @GetMapping("{userId}")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Get asset price")
    public ResponseEntity<AssetPriceDto> getAssetPrice(@PathVariable Long userId, String symbol, String exchange) {
        UserExchangeInfo exchangeInfo = exchangeInfoService.getExchangeInfoByNameAndUserId(userId, exchange);
        return ResponseEntity.ok(featureClients.get(exchange).getAssetPrice(symbol, exchangeInfo));
    }

    @SneakyThrows
    @GetMapping("candles/{userId}")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Get period asset's candle price")
    public ResponseEntity<List<AssetCandleDto>> getAssetPriceCandles(@PathVariable Long userId, String exchange,
                                                                     PeriodAssetPriceCandlesRequest request) {
        UserExchangeInfo exchangeInfo = exchangeInfoService.getExchangeInfoByNameAndUserId(userId, exchange);
        return ResponseEntity.ok(featureClients.get(exchange).getPeriodAssetPriceCandles(request, exchangeInfo));
    }

    @SneakyThrows
    @GetMapping("asset-details")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Get asset details")
    public ResponseEntity<List<AssetContractResponseDto>> getAssetDetails(String exchange) {
        return ResponseEntity.ok(assetContractMapper.getAssetContractDtoList(
                assetContractService.getByExchange(exchange)));
    }

    @SneakyThrows
    @GetMapping("/{userId}/symbol-leverage")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Get symbol leverage")
    public ResponseEntity<LeverageSizeDto> getSymbolLeverage(@PathVariable Long userId, String exchange,
                                                             String symbol) {
        UserExchangeInfo exchangeInfo = exchangeInfoService.getExchangeInfoByNameAndUserId(userId, exchange);
        return ResponseEntity.ok(featureClients.get(exchange).getSymbolLeverage(symbol, exchangeInfo));
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
        UserExchangeInfo exchangeInfo = exchangeInfoService.getExchangeInfoByNameAndUserId(userId, exchange);
        return ResponseEntity.ok(featureClients.get(exchange).updateSymbolLeverage(symbol, leverage, side, exchangeInfo));
    }

    @SneakyThrows
    @GetMapping("symbol-data")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "Asset-price controller", description = "Get symbols' data")
    public ResponseEntity<SymbolResponseDto> getSymbolData(SymbolRequestDto requestDto) {
        return ResponseEntity.ok(assetContractService.getSymbolsByParams(requestDto));
    }

}
