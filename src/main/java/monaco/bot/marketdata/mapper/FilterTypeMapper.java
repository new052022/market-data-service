package monaco.bot.marketdata.mapper;

import monaco.bot.marketdata.dto.FilterTypeResonseDto;
import monaco.bot.marketdata.dto.binance.exchangeInfo.FilterDto;
import monaco.bot.marketdata.model.FilterType;
import monaco.bot.marketdata.service.interfaces.FilterTypeService;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class FilterTypeMapper {

    public List<FilterTypeResonseDto> toFilterTypeResponseDtoList(Set<FilterType> filterTypes) {
        if (CollectionUtils.isEmpty(filterTypes)) {
            return null;
        }
        return filterTypes.stream()
                .map(type -> FilterTypeResonseDto.builder()
                        .id(type.getId())
                        .maxPrice(type.getMaxPrice())
                        .FilterType(type.getFilterType())
                        .minQty(type.getMinQty())
                        .multiplierDown(type.getMultiplierDown())
                        .multiplierUp(type.getMultiplierUp())
                        .multiplierDecimal(type.getMultiplierDecimal())
                        .stepSize(type.getStepSize())
                        .limit(type.getLimit())
                        .maxQty(type.getMaxQty())
                        .minPrice(type.getMinPrice())
                        .notional(type.getNotional())
                        .tickSize(type.getTickSize())
                        .build())
                .collect(Collectors.toList());
    }


    public Set<FilterType> toFilters(List<FilterDto> filters) {
        return filters.stream()
                .map(filter -> FilterType.builder()
                        .FilterType(filter.getFilterType())
                        .limit((double) filter.getLimit())
                        .maxPrice(filter.getMaxPrice() != null ? Double.parseDouble(filter.getMaxPrice()) : null)
                        .maxQty(filter.getMaxQty() != null ? Double.parseDouble(filter.getMaxQty()) : null)
                        .minPrice(filter.getMinPrice() != null ? Double.parseDouble(filter.getMinPrice()) : null)
                        .minQty(filter.getMinQty() != null ? Double.parseDouble(filter.getMinQty()) : null)
                        .multiplierDecimal(filter.getMultiplierDecimal() != null ? Double.parseDouble(filter.getMultiplierDecimal()) : null)
                        .multiplierDown(filter.getMultiplierDown() != null ? Double.parseDouble(filter.getMultiplierDown()) : null)
                        .multiplierUp(filter.getMultiplierUp() != null ? Double.parseDouble(filter.getMultiplierUp()) : null)
                        .stepSize(filter.getStepSize() != null ? Double.parseDouble(filter.getStepSize()) : null)
                        .tickSize(filter.getTickSize() != null ? Double.parseDouble(filter.getTickSize()) : null)
                        .notional(filter.getNotional() != null ? Double.parseDouble(filter.getNotional()) : null)
                        .build())
                .collect(Collectors.toSet());
    }

}
