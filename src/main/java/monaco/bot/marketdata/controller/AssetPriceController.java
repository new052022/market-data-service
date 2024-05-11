package monaco.bot.marketdata.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import monaco.bot.marketdata.client.impl.BingxFeatureClient;
import monaco.bot.marketdata.dto.AssetPriceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/asset-price")
@Tag(name = "Asset-price controller")
public class AssetPriceController {

    private final BingxFeatureClient bingxFeatureClient;

//    @GetMapping("{userId}")
//    @ApiResponse(responseCode = "200", description = "Success")
//    @Operation(tags = "User-info controller", description = "Get api user information")
//    public ResponseEntity<AssetPriceDto> getUserInfo(@PathVariable Long userId, String symbol, String exchange) {
//        return ResponseEntity.ok(response);
//    }

}
