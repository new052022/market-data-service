package monaco.bot.marketdata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import monaco.bot.marketdata.dto.binance.user_trades.UserTradesHistoryResponseDto;
import monaco.bot.marketdata.service.interfaces.UserExchangeDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-data")
@Tag(name = "User-exchange-data controller")
public class UserExchangeDataController {

    private final UserExchangeDataService userExchangeDataService;

    @SneakyThrows
    @GetMapping("{userId}")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "User-exchange-data controller", description = "Get user's data")
    public ResponseEntity<List<UserTradesHistoryResponseDto>> getAssetsPrices(@PathVariable Long userId,
                                                                              @RequestParam(required = false) String exchange) {
        return ResponseEntity.ok(userExchangeDataService.getUsersTradeHistory(userId, exchange));
    }

}
