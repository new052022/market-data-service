package monaco.bot.marketdata.service.interfaces;

import monaco.bot.marketdata.model.FilterType;

import java.util.List;
import java.util.Set;

public interface FilterTypeService {

    Set<FilterType> saveAll(List<FilterType> filterTypeList);

}
