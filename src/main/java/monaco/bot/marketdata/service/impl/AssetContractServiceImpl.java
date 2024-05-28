package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import monaco.bot.marketdata.client.interfaces.MarketDataClient;
import monaco.bot.marketdata.dto.AssetCandleDto;
import monaco.bot.marketdata.dto.ExchangeSymbolsRequestDto;
import monaco.bot.marketdata.dto.PeriodAssetPriceCandlesRequest;
import monaco.bot.marketdata.dto.SymbolParamsDto;
import monaco.bot.marketdata.dto.SymbolRequestDto;
import monaco.bot.marketdata.dto.SymbolResponseDto;
import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.model.UserExchangeInfo;
import monaco.bot.marketdata.model.UserSymbolLeverage;
import monaco.bot.marketdata.repository.AssetContractRepository;
import monaco.bot.marketdata.service.interfaces.AssetContractService;
import monaco.bot.marketdata.service.interfaces.UserExchangeInfoService;
import monaco.bot.marketdata.service.interfaces.UserInfoService;
import monaco.bot.marketdata.service.interfaces.UserSymbolLeverageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetContractServiceImpl implements AssetContractService {

    private final AssetContractRepository assetContractRepository;

    private final UserExchangeInfoService exchangeService;

    private final Map<String, MarketDataClient> marketDataClients;

    private final UserSymbolLeverageService userSymbolLeverageService;

    private final UserInfoService userInfoService;

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

    @Override
    public SymbolResponseDto getSymbolsByParams(SymbolRequestDto requestDto) {
        Map<String, UserExchangeInfo> exchangeMap = userInfoService.getUser(requestDto.getUserId()).getExchanges()
                .stream()
                .filter(exchangeInfo -> requestDto.getExchanges().contains(exchangeInfo.getExchange().getName()))
                .collect(Collectors.toMap(exchangeInfo -> exchangeInfo.getExchange().getName(), exchange -> exchange));
        List<UserSymbolLeverage> symbolLeverages = userSymbolLeverageService.getSymbolsByUserIdAndLeverage(
                requestDto.getUserId(), requestDto.getLeverage(), requestDto.getExchanges());
        Map<String, List<AssetContract>> assets = assetContractRepository.getAssetContractsByExchangeNameIn(
                        new ArrayList<>(exchangeMap.keySet())).stream()
                .collect(groupingBy(symbol -> symbol.getExchange().getName()));
        Map<String, List<AssetCandleDto>> exchangeCandles = symbolLeverages.stream()
                .map(symbol -> {
                    MarketDataClient client = marketDataClients.get(symbol.getExchange().getName());
                    return client.getPeriodAssetPriceCandles(
                            PeriodAssetPriceCandlesRequest.builder()
                                    .symbol(symbol.getSymbol())
                                    .limit(requestDto.getLimit())
                                    .interval(requestDto.getInterval())
                                    .endTime(requestDto.getEndTime())
                                    .startTime(requestDto.getStartTime())
                                    .build(), exchangeMap.get(symbol.getExchange().getName()));
                })
                .flatMap(Collection::stream)
                .collect(groupingBy(AssetCandleDto::getExchange));
        Map<String, List<AssetCandleDto>> candleMap = this.filterByVolume(exchangeCandles, requestDto.getVolume());
        List<ExchangeSymbolsRequestDto> exchangeSymbols = this.convertToExchangeSymbolsRequestDto(candleMap, assets);
        log.info("[TRADING BOT] Time: {} | Market-data-service | getSymbolsByParams" +
                        " | exchanges' number : {} | symbols' number for first exchange: {} | symbols' number for second exchange if exists: {} " +
                        "| action: {}",
                Timestamp.from(Instant.now()), exchangeSymbols.size(), exchangeSymbols.getFirst().getSymbols().stream()
                        .map(SymbolParamsDto::getSymbol)
                        .distinct()
                        .toList().size(), exchangeSymbols.getLast().getSymbols().stream()
                        .map(SymbolParamsDto::getSymbol)
                        .distinct()
                        .toList().size(),
                "get symbols by leverage and volume params");
        return SymbolResponseDto.builder()
                .volume(requestDto.getVolume())
                .userId(requestDto.getUserId())
                .leverage(requestDto.getLeverage())
                .exchanges(exchangeSymbols)
                .build();
    }

    @Override
    public List<AssetContract> getAll() {
        return assetContractRepository.findAll();
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

    private List<ExchangeSymbolsRequestDto> convertToExchangeSymbolsRequestDto(Map<String,
            List<AssetCandleDto>> exchangeCandles, Map<String, List<AssetContract>> assets) {
        return exchangeCandles.keySet().stream()
                .map(exchange -> ExchangeSymbolsRequestDto.builder()
                        .exchange(exchange)
                        .symbols(this.retrieveSymbolParams(exchangeCandles.get(exchange), assets.get(exchange)))
                        .build())
                .collect(Collectors.toList());
    }

    private List<SymbolParamsDto> retrieveSymbolParams
            (List<AssetCandleDto> assetCandleDtos, List<AssetContract> assetContracts) {
        Map<String, AssetContract> assets = assetContracts.stream()
                .filter(asset -> asset.getBinanceContractType() == null ||
                        asset.getBinanceContractType().equalsIgnoreCase("PERPETUAL"))
                .collect(Collectors.toMap(AssetContract::getSymbol, Function.identity()));
        return assetCandleDtos.stream()
                .filter(asset -> assets.containsKey(asset.getSymbol()))
                .map(symbol -> SymbolParamsDto.builder()
                        .symbol(symbol.getSymbol())
                        .openPrice(symbol.getOpen())
                        .openTime(symbol.getTime())
                        .volume(symbol.getVolume())
                        .lowPrice(symbol.getLow())
                        .stepSize(assets.get(symbol.getSymbol()).getTradeMinQuantity() != null ?
                                assets.get(symbol.getSymbol()).getTradeMinQuantity() : assets.get(symbol.getSymbol()).getFilters().stream()
                                .filter(filter -> filter.getFilterType().equalsIgnoreCase("LOT_SIZE"))
                                .findFirst().get().getStepSize())
                        .tickSize(assets.get(symbol.getSymbol()).getTradeMinUSDT() != null ?
                                assets.get(symbol.getSymbol()).getTradeMinUSDT() : assets.get(symbol.getSymbol()).getFilters().stream()
                                .filter(filter -> filter.getFilterType().equalsIgnoreCase("PRICE_FILTER"))
                                .findFirst().get().getTickSize())
                        .highPrice(symbol.getHigh())
                        .closePrice(symbol.getClose())
                        .build())
                .collect(Collectors.toList());
    }

    private Map<String, List<AssetCandleDto>> filterByVolume
            (Map<String, List<AssetCandleDto>> exchangeCandles, Long volume) {
        log.info("[TRADING BOT] Time: {} | Market-data-service | filterByVolume" +
                        " | asset's volume : {} | action: {}",
                Timestamp.from(Instant.now()), volume, "filter assets by volume");
        return exchangeCandles.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        exchangeEntry -> {
                            List<AssetCandleDto> candles = exchangeEntry.getValue();
                            Map<String, Double> averageVolumesBySymbol = candles.stream()
                                    .collect(Collectors.groupingBy(
                                            AssetCandleDto::getSymbol,
                                            Collectors.averagingDouble(AssetCandleDto::getVolume)
                                    ));
                            return candles.stream()
                                    .filter(candle -> averageVolumesBySymbol.get(candle.getSymbol()) >= volume)
                                    .collect(Collectors.toList());
                        }
                ));
    }

}
