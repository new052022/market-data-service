package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import monaco.bot.marketdata.model.Exchange;
import monaco.bot.marketdata.repository.ExchangeRepository;
import monaco.bot.marketdata.service.interfaces.ExchangeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeRepository exchangeRepository;

    @Override
    public Exchange getExchangeByName(String name) {
        return exchangeRepository.getExchangeByName(name).orElseThrow(() ->
                new NoSuchElementException(String.format("Exchange with name %s doesn't exist", name)));
    }

    @Override
    public List<Exchange> getAll() {
        return exchangeRepository.findAll();
    }

}
