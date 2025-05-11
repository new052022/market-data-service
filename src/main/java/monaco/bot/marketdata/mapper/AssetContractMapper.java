package monaco.bot.marketdata.mapper;

import monaco.bot.marketdata.dto.AssetContractResponseDto;
import monaco.bot.marketdata.dto.AssetDetailsDto;
import monaco.bot.marketdata.dto.binance.LeverageDto;
import monaco.bot.marketdata.dto.binance.exchangeInfo.SymbolDto;
import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.model.Exchange;
import monaco.bot.marketdata.model.FilterType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class AssetContractMapper {

    @Autowired
    private FilterTypeMapper filterTypeMapper;

    @Autowired
    private ExchangeMapper exchangeMapper;

    public List<AssetContractResponseDto> getAssetContractDtoList(List<AssetContract> assetContracts) {
        return assetContracts.stream()
                .map(contract -> AssetContractResponseDto.builder()
                        .id(contract.getId())
                        .asset(contract.getAsset())
                        .binanceContractType(contract.getBinanceContractType())
                        .currency(contract.getCurrency())
                        .maxShortLeverage(contract.getMaxShortLeverage())
                        .maxLongLeverage(contract.getMaxLongLeverage())
                        .exchange(exchangeMapper.toExchangeMapperResponse(contract.getExchange()))
                        .quantityPrecision(contract.getQuantityPrecision())
                        .symbol(contract.getSymbol())
                        .takerFeeRate(contract.getTakerFeeRate())
                        .pricePrecision(contract.getPricePrecision())
                        .tradeMinLimit(contract.getTradeMinLimit())
                        .tradeMinQuantity(contract.getTradeMinQuantity())
                        .tradeMinUSDT(contract.getTradeMinUSDT())
                        .feeRate(contract.getFeeRate())
                        .makerFeeRate(contract.getMakerFeeRate())
                        .filters(filterTypeMapper.toFilterTypeResponseDtoList(contract.getFilters()))
                        .build())
                .collect(Collectors.toList());
    }

    public List<AssetContract> fromAssetDetailsDtoListToAssetContractList(List<AssetDetailsDto> assets,
                                                                          Exchange exchange) {
        return assets.stream()
                .map(asset -> AssetContract.builder()
                        .asset(asset.getAsset())
                        .feeRate(asset.getFeeRate())
                        .currency(asset.getCurrency())
                        .makerFeeRate(asset.getMakerFeeRate())
                        .exchange(exchange)
                        .maxLongLeverage(asset.getMaxLongLeverage())
                        .maxShortLeverage(asset.getMaxShortLeverage())
                        .pricePrecision(asset.getPricePrecision())
                        .quantityPrecision(asset.getQuantityPrecision())
                        .symbol(asset.getSymbol())
                        .takerFeeRate(asset.getTakerFeeRate())
                        .tradeMinLimit(asset.getTradeMinLimit())
                        .tradeMinUSDT(asset.getTradeMinUSDT())
                        .tradeMinQuantity(asset.getTradeMinQuantity())
                        .filters(this.addFilters(asset))
                        .build())
                .collect(Collectors.toList());
    }

    private Set<FilterType> addFilters(AssetDetailsDto asset) {
        double quantityPrecision = asset.getQuantityPrecision();
        double pricePrecision = asset.getPricePrecision();

        // Вычисляем stepSize как 0.001, если precision = 3
        double stepSize = 1 / Math.pow(10, quantityPrecision);

        // Вычисляем tickSize как 0.01, если precision = 2
        double tickSize = 1 / Math.pow(10, pricePrecision);

        FilterType marketLotSizeFilter = new FilterType();
        marketLotSizeFilter.setFilterType("MARKET_LOT_SIZE");
        marketLotSizeFilter.setMinQty(stepSize);
        marketLotSizeFilter.setStepSize(stepSize);

        FilterType priceFilter = new FilterType();
        priceFilter.setFilterType("PRICE_FILTER");
        priceFilter.setTickSize(tickSize);

        return Set.of(marketLotSizeFilter, priceFilter);
    }

    public AssetContract toAssetCandleDto(SymbolDto symbol, LeverageDto leverageDto, Exchange exchange) {
        return AssetContract.builder()
                .symbol(symbol.getSymbol())
                .filters(filterTypeMapper.toFilters(symbol.getFilters()))
                .binanceContractType(symbol.getContractType())
                .currency(symbol.getQuoteAsset())
                .asset(symbol.getBaseAsset())
                .maxShortLeverage(Long.valueOf(leverageDto.getBrackets().get(0).getInitialLeverage()))
                .maxLongLeverage(Long.valueOf(leverageDto.getBrackets().get(0).getInitialLeverage()))
                .exchange(exchange)
                .tradeMinQuantity(this.getTradeMinQuantity(symbol))
                .tradeMinUSDT(this.getTradeMinUSDT(symbol))
                .build();
    }

    private Double getTradeMinQuantity(SymbolDto symbol) {
        return symbol.getFilters().stream()
                .filter(filter -> filter.getFilterType().equals("LOT_SIZE"))
                .map(filter -> Double.valueOf(filter.getMinQty()))
                .findFirst().orElse(0.0);
    }

    private Double getTradeMinUSDT(SymbolDto symbol) {
        return symbol.getFilters().stream()
                .filter(filter -> filter.getFilterType().equals("MIN_NOTIONAL"))
                .map(filter -> Double.valueOf(filter.getNotional()))
                .findFirst().orElse(0.0);
    }

}
