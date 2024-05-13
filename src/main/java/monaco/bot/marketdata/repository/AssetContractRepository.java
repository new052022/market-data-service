package monaco.bot.marketdata.repository;

import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface AssetContractRepository extends JpaRepository<AssetContract, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    Integer deleteAllByExchange(Exchange exchange);

}
