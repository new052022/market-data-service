package monaco.bot.marketdata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import monaco.bot.marketdata.dto.NewUserExchangeRequestDto;
import monaco.bot.marketdata.dto.UserExchangeResponseDto;
import monaco.bot.marketdata.mapper.UserExchangeInfoMapper;
import monaco.bot.marketdata.model.UserExchangeInfo;
import monaco.bot.marketdata.service.interfaces.UserExchangeInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-exchange-info")
@Tag(name = "User-exchange-info controller")
public class UserExchangeInfoController {

    private final UserExchangeInfoService exchangeInfoService;

    private final UserExchangeInfoMapper exchangeInfoMapper;

    @PostMapping
    @ApiResponse(responseCode = "201", description = "User exchange info was added")
    @Operation(tags = "User-exchange-info controller", description = "Add user's exchange info")
    public ResponseEntity<UserExchangeResponseDto> addExchangeInfoToUser(@RequestBody
                                                                                NewUserExchangeRequestDto dto) {
        UserExchangeInfo exchangeInfo = exchangeInfoMapper.toExchangeInfo(dto);
        UserExchangeInfo savedExchangeInfo = exchangeInfoService.save(exchangeInfo);
        UserExchangeResponseDto response = exchangeInfoMapper.toExchangeInfoResponseDto(savedExchangeInfo);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("{userId}")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "User-exchange-info controller", description = "Get user's exchanges")
    public ResponseEntity<List<UserExchangeResponseDto>> getUserInfo(@PathVariable Long userId) {
        List<UserExchangeInfo> exchanges = exchangeInfoService.getExchanges(userId);
        List<UserExchangeResponseDto> response = exchangeInfoMapper.toExchangeInfoResponseList(exchanges);
        return ResponseEntity.ok(response);
    }

}
