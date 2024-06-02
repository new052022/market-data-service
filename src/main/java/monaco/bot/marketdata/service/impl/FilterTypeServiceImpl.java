package monaco.bot.marketdata.service.impl;

import lombok.RequiredArgsConstructor;
import monaco.bot.marketdata.model.FilterType;
import monaco.bot.marketdata.repository.FilterTypeRepository;
import monaco.bot.marketdata.service.interfaces.FilterTypeService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilterTypeServiceImpl implements FilterTypeService {

    private final FilterTypeRepository filterTypeRepository;

    public Set<FilterType> saveAll(List<FilterType> filterTypeList) {
        return new HashSet<>(filterTypeRepository.saveAll(filterTypeList));
    }

}
