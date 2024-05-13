package monaco.bot.marketdata.service.interfaces;

import monaco.bot.marketdata.model.UserExchangeInfo;

import java.util.List;

public interface UserExchangeInfoService {

    UserExchangeInfo save(UserExchangeInfo exchangeInfo);

    List<UserExchangeInfo> getExchanges(Long userId);

    UserExchangeInfo getExchangeInfoByNameAndUserId(Long userId, String exchange);

    List<UserExchangeInfo> findById(long userId);
}
