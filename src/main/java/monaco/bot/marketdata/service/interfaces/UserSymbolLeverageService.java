package monaco.bot.marketdata.service.interfaces;

import monaco.bot.marketdata.model.UserSymbolLeverage;

import java.util.List;

public interface UserSymbolLeverageService {

    void updateUserSymbolLeverages(Long userId);

    List<UserSymbolLeverage> getSymbolsByUserIdAndLeverage(Long userId, Long leverage, List<String> exchanges);

    List<UserSymbolLeverage> collectUserSymbolLeverages(Long userId, List<String> exchange);

}
