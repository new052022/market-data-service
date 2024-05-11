package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import monaco.bot.marketdata.model.UserExchangeInfo;
import monaco.bot.marketdata.repository.UserExchangeInfoRepository;
import monaco.bot.marketdata.service.interfaces.UserExchangeInfoService;
import monaco.bot.marketdata.service.interfaces.UserInfoService;
import monaco.bot.marketdata.util.EncryptDecryptGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserExchangeInfoServiceImpl implements UserExchangeInfoService {

    private final UserExchangeInfoRepository exchangeInfoRepository;

    private final EncryptDecryptGenerator encryptDecryptGenerator;

    private final UserInfoService userInfoService;

    @Override
    public UserExchangeInfo save(UserExchangeInfo exchangeInfo) {
        this.encodeExchangeData(exchangeInfo);
        return exchangeInfoRepository.save(exchangeInfo);
    }

    @Override
    public List<UserExchangeInfo> getExchanges(Long userId) {
        return exchangeInfoRepository.findByUserInfoId(userId);
    }

    @Override
    public UserExchangeInfo getExchangeInfoByNameAndUserId(Long userId, String exchange) {
        return userInfoService.getUser(userId).getExchanges().stream()
                .filter(exch -> exch.getExchangeName().equalsIgnoreCase(exchange))
                .findFirst().orElseThrow(() -> new NoSuchElementException(
                        String.format("User with id №%d doesn't have info about exchange - %s", userId, exchange)));
    }

    @SneakyThrows
    private void encodeExchangeData(UserExchangeInfo exchangeInfo) {
        exchangeInfo.setApiKey(encryptDecryptGenerator.encryptData(exchangeInfo.getApiKey()));
        exchangeInfo.setSecretKey(encryptDecryptGenerator.encryptData(exchangeInfo.getSecretKey()));
    }

}
