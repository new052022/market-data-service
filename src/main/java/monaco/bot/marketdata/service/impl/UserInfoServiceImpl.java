package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import monaco.bot.marketdata.model.UserInfo;
import monaco.bot.marketdata.repository.UserInfoRepository;
import monaco.bot.marketdata.service.interfaces.UserInfoService;
import monaco.bot.marketdata.util.EncryptDecryptGenerator;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository userInfoRepository;

    private final EncryptDecryptGenerator encryptDecryptGenerator;

    @Override
    public UserInfo save(UserInfo userInfo) {
        this.encodeUserData(userInfo);
        return userInfoRepository.save(userInfo);
    }

    @Override
    public UserInfo getUser(Long id) {
        return userInfoRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("User with id № %d doesn't exist", id)));
    }

    @SneakyThrows
    private void encodeUserData(UserInfo userInfo) {
        userInfo.setApiKey(encryptDecryptGenerator.encryptData(userInfo.getApiKey()));
        userInfo.setSecretKey(encryptDecryptGenerator.encryptData(userInfo.getSecretKey()));
    }

}
