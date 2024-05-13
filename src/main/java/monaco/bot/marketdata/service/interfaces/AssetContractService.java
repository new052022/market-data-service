package monaco.bot.marketdata.service.interfaces;

import monaco.bot.marketdata.model.AssetContract;

import java.util.List;

public interface AssetContractService {

    List<AssetContract> saveAll(List<AssetContract> assetContracts);

}
