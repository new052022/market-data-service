package monaco.bot.marketdata.mapper;

import monaco.bot.marketdata.dto.ExchangeResponseDto;
import monaco.bot.marketdata.model.Exchange;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExchangeMapper {

    ExchangeResponseDto toExchangeMapperResponse(Exchange exchange);

}
