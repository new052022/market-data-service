package monaco.bot.marketdata.repository;

import monaco.bot.marketdata.model.UserExchangeInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserExchangeInfoRepository extends JpaRepository<UserExchangeInfo, Long> {

    @EntityGraph(value = "user-exchange", type = EntityGraph.EntityGraphType.FETCH)
    List<UserExchangeInfo> findByUserInfoId(Long userId);

}
