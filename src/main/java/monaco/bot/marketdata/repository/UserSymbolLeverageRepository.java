package monaco.bot.marketdata.repository;

import monaco.bot.marketdata.model.UserSymbolLeverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSymbolLeverageRepository extends JpaRepository<UserSymbolLeverage, Long>  {

    @Query("SELECT usl FROM UserSymbolLeverage usl " +
            "WHERE usl.userInfo.id = :userId " +
            "AND usl.longLeverage >= :leverage " +
            "AND usl.shortLeverage >= :leverage " +
            "AND usl.exchange.name IN :exchanges")
    List<UserSymbolLeverage> getUserSymbolsByParams(@Param("userId") Long userId,
                                                    @Param("leverage") Long leverage,
                                                    @Param("exchanges") List<String> exchanges);

}
