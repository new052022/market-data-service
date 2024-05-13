package monaco.bot.marketdata.repository;

import monaco.bot.marketdata.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    Optional<Exchange> getExchangeByName(String name);

}
