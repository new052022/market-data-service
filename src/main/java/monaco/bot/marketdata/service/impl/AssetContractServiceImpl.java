package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import monaco.bot.marketdata.client.interfaces.MarketDataClient;
import monaco.bot.marketdata.dto.AssetContractResponseDto;
import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.model.Exchange;
import monaco.bot.marketdata.model.UserExchangeInfo;
import monaco.bot.marketdata.repository.AssetContractRepository;
import monaco.bot.marketdata.service.interfaces.AssetContractService;
import monaco.bot.marketdata.service.interfaces.UserExchangeInfoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetContractServiceImpl implements AssetContractService {

    private final AssetContractRepository assetContractRepository;

    private final UserExchangeInfoService exchangeService;

    private final Map<String, MarketDataClient> marketDataClients;

    public List<AssetContract> saveAll(List<AssetContract> assetContracts) {
        for (AssetContract assetContract : assetContracts) {
            if (!CollectionUtils.isEmpty(assetContract.getFilters())) {
                assetContract.setFilters();
            }
        }
        return assetContractRepository.saveAll(assetContracts);
    }

    @Override
    public List<AssetContract> getByExchange(String name) {
        return assetContractRepository.getAssetContractsByExchangeName(name);
    }

    @Scheduled(cron = "0 0 */12 * * *")
    @Transactional
    public void updateAssetContracts() {
        LocalDateTime startTime = LocalDateTime.now();
        log.info("Updating asset contracts from external exchange api started at {}", startTime);
        assetContractRepository.deleteAll();
        List<AssetContract> assetsToSave = new ArrayList<>();
        try {
            Map<String, UserExchangeInfo> exchanges = exchangeService.findById(1L).stream()
                    .collect(Collectors.toMap(exchange -> exchange.getExchange().getName(), Function.identity()));
            for (UserExchangeInfo exchange : exchanges.values()) {
                MarketDataClient client = marketDataClients.get(exchange.getExchange().getName());
                List<AssetContract> assetDetails = client.getAssetDetails(exchange);
                assetsToSave.addAll(assetDetails);
            }
        } catch (Exception exception) {
            log.error("Updating asset contracts from external exchange API failed: {}", exception.getMessage());
        }
        this.saveAll(assetsToSave);
        LocalDateTime endTime = LocalDateTime.now();
        log.info("Updating asset contracts from external exchange api successfully ended at {} and took {} seconds",
                startTime, Duration.between(startTime, endTime));
    }

}
