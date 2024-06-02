package monaco.bot.marketdata.repository;

import monaco.bot.marketdata.model.FilterType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilterTypeRepository extends JpaRepository<FilterType, Long> {
}
