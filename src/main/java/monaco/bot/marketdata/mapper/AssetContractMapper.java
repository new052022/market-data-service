package monaco.bot.marketdata.mapper;

import monaco.bot.marketdata.dto.AssetDetailsDto;
import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.model.Exchange;
import monaco.bot.marketdata.service.interfaces.ExchangeService;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class AssetContractMapper {

    @Autowired
    private ExchangeService exchangeService;

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

}
