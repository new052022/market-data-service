package monaco.bot.marketdata.service.interfaces;

import monaco.bot.marketdata.model.Exchange;

import java.util.List;

public interface ExchangeService {

    Exchange getExchangeByName(String name);

    List<Exchange> getAll();
}
