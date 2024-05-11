package monaco.bot.marketdata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import monaco.bot.marketdata.dto.UserInfoRequestDto;
import monaco.bot.marketdata.dto.UserInfoResponseDto;
import monaco.bot.marketdata.mapper.UserInfoMapper;
import monaco.bot.marketdata.model.UserInfo;
import monaco.bot.marketdata.service.interfaces.UserInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-info")
@Tag(name = "User-info controller")
public class UserInfoController {

    private final UserInfoService userInfoService;

    private final UserInfoMapper userInfoMapper;

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Api user was created")
    @Operation(tags = "User-info controller", description = "Create api user")
    public ResponseEntity<UserInfoResponseDto> createApiUser(@RequestBody UserInfoRequestDto dto) {
        UserInfo newUser = userInfoMapper.toUserInfo(dto);
        UserInfo savedUser = userInfoService.save(newUser);
        UserInfoResponseDto response = userInfoMapper.toUserInfoResponseDto(savedUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    @ApiResponse(responseCode = "200", description = "Success")
    @Operation(tags = "User-info controller", description = "Get api user information")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@PathVariable Long id) {
        UserInfo user = userInfoService.getUser(id);
        UserInfoResponseDto response = userInfoMapper.toUserInfoResponseDto(user);
        return ResponseEntity.ok(response);
    }

}
