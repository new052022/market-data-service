package monaco.bot.marketdata.repository;

import monaco.bot.marketdata.model.UserExchangeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserExchangeInfoRepository extends JpaRepository<UserExchangeInfo, Long> {

    List<UserExchangeInfo> findByUserInfoId(Long userId);

}
