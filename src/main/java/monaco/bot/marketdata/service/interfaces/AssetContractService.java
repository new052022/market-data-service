package monaco.bot.marketdata.service.interfaces;

import monaco.bot.marketdata.dto.AssetContractResponseDto;
import monaco.bot.marketdata.model.AssetContract;
import monaco.bot.marketdata.model.UserExchangeInfo;

import java.util.List;

public interface AssetContractService {

    List<AssetContract> saveAll(List<AssetContract> assetContracts);

    List<AssetContract> getByExchange(String name);

}
