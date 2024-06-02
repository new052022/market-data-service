package monaco.bot.marketdata.service.interfaces;

import monaco.bot.marketdata.dto.SymbolRequestDto;
import monaco.bot.marketdata.dto.SymbolResponseDto;
import monaco.bot.marketdata.model.AssetContract;

import java.util.List;

public interface AssetContractService {

    List<AssetContract> saveAll(List<AssetContract> assetContracts);

    List<AssetContract> getByExchange(String name);

    SymbolResponseDto getSymbolsByParams(SymbolRequestDto requestDto);

    List<AssetContract> getAll();

}
