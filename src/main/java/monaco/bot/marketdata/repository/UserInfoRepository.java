package monaco.bot.marketdata.repository;

import monaco.bot.marketdata.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
}
