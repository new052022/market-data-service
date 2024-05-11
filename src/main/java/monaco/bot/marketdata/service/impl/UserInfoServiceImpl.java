package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import monaco.bot.marketdata.model.UserInfo;
import monaco.bot.marketdata.repository.UserInfoRepository;
import monaco.bot.marketdata.service.interfaces.UserInfoService;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository userInfoRepository;

    @Override
    public UserInfo save(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    @Override
    public UserInfo getUser(Long id) {
        return userInfoRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("User with id № %d doesn't exist", id)));
    }

}
