package monaco.bot.marketdata.client.impl;

import monaco.bot.marketdata.dto.UserExchangeResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "usersClient", url = "${users-service.url}")
public interface UsersClient {

    @GetMapping("${users-service.user-exchange-endpoint}/{userId}")
    List<UserExchangeResponseDto> getUserExchangeInfo(@PathVariable("userId") Long userId);

}
