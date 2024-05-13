package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import monaco.bot.marketdata.client.interfaces.MarketDataClient;
import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.model.Exchange;
import monaco.bot.marketdata.model.UserExchangeInfo;
import monaco.bot.marketdata.repository.AssetContractRepository;
import monaco.bot.marketdata.service.interfaces.AssetContractService;
import monaco.bot.marketdata.service.interfaces.UserExchangeInfoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
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

    public void deleteAllByExchange(Exchange exchange) {
        assetContractRepository.deleteAllByExchange(exchange);
    }

    public List<AssetContract> saveAll(List<AssetContract> assetContracts) {
        return assetContractRepository.saveAll(assetContracts);
    }

    @Scheduled(cron = "0 0 */12 * * *")
    @Transactional
    public void updateAssetContracts() {
        LocalDateTime startTime = LocalDateTime.now();
        log.info("Updating asset contracts from external exchange api started at {}", startTime);
        try {
            Map<String, UserExchangeInfo> exchanges = exchangeService.findById(1L).stream()
                    .collect(Collectors.toMap(exchange -> exchange.getExchange().getName(), Function.identity()));
            for (UserExchangeInfo exchange : exchanges.values()) {
                MarketDataClient client = marketDataClients.get(exchange.getExchange().getName());
                List<AssetContract> assetDetails = client.getAssetDetails(exchange);
                this.deleteAllByExchange(exchange.getExchange());
                this.saveAll(assetDetails);
            }
        } catch (Exception exception) {
            log.error("Updating asset contracts from external exchange API failed: {}", exception.getMessage());
        }
        LocalDateTime endTime = LocalDateTime.now();
        log.info("Updating asset contracts from external exchange api successfully ended at {} and took {} seconds",
                startTime, Duration.between(startTime, endTime));
    }

}
