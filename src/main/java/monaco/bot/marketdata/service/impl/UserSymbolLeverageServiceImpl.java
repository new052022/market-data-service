package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import monaco.bot.marketdata.client.interfaces.MarketDataClient;
import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.model.UserSymbolLeverage;
import monaco.bot.marketdata.repository.UserSymbolLeverageRepository;
import monaco.bot.marketdata.service.interfaces.AssetContractService;
import monaco.bot.marketdata.service.interfaces.ExchangeService;
import monaco.bot.marketdata.service.interfaces.UserExchangeInfoService;
import monaco.bot.marketdata.service.interfaces.UserInfoService;
import monaco.bot.marketdata.service.interfaces.UserSymbolLeverageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class UserSymbolLeverageServiceImpl implements UserSymbolLeverageService {

    private final UserSymbolLeverageRepository userSymbolLeverageRepository;

    private final UserExchangeInfoService userExchangeInfoService;

    private AssetContractService assetContractService;

    private final UserInfoService userInfoService;

    private final Map<String, MarketDataClient> featureClients;

    private final ExchangeService exchangeService;

    public void updateUserSymbolLeverages(Long userId) {

    }

    @Override
    public List<UserSymbolLeverage> getSymbolsByUserIdAndLeverage(Long userId, Long leverage, List<String> exchange) {
        List<UserSymbolLeverage> userSymbolsByParams = userSymbolLeverageRepository.getUserSymbolsByParams(
                userId, leverage, exchange);
        if (CollectionUtils.isEmpty(userSymbolsByParams)) {
            return userSymbolLeverageRepository.saveAll(this.collectUserSymbolLeverages(userId, exchange)).stream()
                    .filter(symbolLeverage ->symbolLeverage.getLongLeverage() != null &&
                            symbolLeverage.getShortLeverage() != null &&
                            symbolLeverage.getShortLeverage().equals(leverage) &&
                            symbolLeverage.getLongLeverage().equals(leverage))
                    .collect(Collectors.toList());
        } else {
            return userSymbolsByParams;
        }
    }

    @Override
    public List<UserSymbolLeverage> collectUserSymbolLeverages(Long userId, List<String> exchanges) {
        Map<String, List<AssetContract>> exchangeAssetMap = assetContractService.getAll().stream()
                .collect(groupingBy(asset -> asset.getExchange().getName()));
        return exchanges.stream()
                .flatMap(exchange -> {
                    List<AssetContract> assetContracts = exchangeAssetMap.get(exchange);
                    return assetContracts.stream()
                            .map(asset -> featureClients.get(exchange).getSymbolLeverage(asset.getSymbol(),
                                    userExchangeInfoService.getExchangeInfoByNameAndUserId(userId, exchange)));
                })
                .map(leverageSize -> UserSymbolLeverage.builder()
                        .symbol(leverageSize.getSymbol())
                        .shortLeverage(leverageSize.getShortLeverage() != null ? leverageSize.getShortLeverage() :
                                leverageSize.getMaxShortLeverage())
                        .longLeverage(leverageSize.getLongLeverage() != null ? leverageSize.getLongLeverage() :
                                leverageSize.getMaxLongLeverage())
                        .exchange(exchangeService.getExchangeByName(leverageSize.getExchange()))
                        .userInfo(userInfoService.getUser(userId))
                        .build())
                .collect(Collectors.toList());
    }

    @Lazy
    @Autowired
    public void setAssetContractService(AssetContractService assetContractService) {
        this.assetContractService = assetContractService;
    }

}
