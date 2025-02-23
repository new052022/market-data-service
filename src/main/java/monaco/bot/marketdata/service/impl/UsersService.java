package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import monaco.bot.marketdata.client.impl.UsersClient;
import monaco.bot.marketdata.dto.UserExchangeResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersClient usersClient;

    public UserExchangeResponseDto getUserExchangeInfoByUserId(Long userId, String exchange){
        return this.getUsersExchanges(userId).stream()
                .filter(userExchange -> userExchange.getExchangeName().equalsIgnoreCase(exchange))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException(String.format(
                                "User with id %d doesn't have data related with exchange %s", userId, exchange)));
    }

    public List<UserExchangeResponseDto> getUsersExchanges(Long userId){
        return usersClient.getUserExchangeInfo(userId);
    }

}
