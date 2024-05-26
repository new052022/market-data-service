package monaco.bot.marketdata.repository;

import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.model.Exchange;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetContractRepository extends JpaRepository<AssetContract, Long> {

    @EntityGraph(attributePaths = {"exchange", "filters"})
    List<AssetContract> getAssetContractsByExchangeName(String name);

    List<AssetContract> getAssetContractsByExchangeNameIn(List<String> exchanges);

}
