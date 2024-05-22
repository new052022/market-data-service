package monaco.bot.marketdata.mapper;

import monaco.bot.marketdata.dto.AssetContractResponseDto;
import monaco.bot.marketdata.dto.AssetDetailsDto;
import monaco.bot.marketdata.dto.binance.LeverageDto;
import monaco.bot.marketdata.dto.binance.exchangeInfo.SymbolDto;
import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.model.Exchange;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
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
                        .build())
                .collect(Collectors.toList());
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
                .build();
    }

}
