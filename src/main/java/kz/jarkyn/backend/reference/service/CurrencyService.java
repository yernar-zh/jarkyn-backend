
package kz.jarkyn.backend.reference.service;


import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.reference.mapper.CurrencyMapper;
import kz.jarkyn.backend.reference.model.CurrencyEntity;
import kz.jarkyn.backend.reference.model.dto.CurrencyResponse;
import kz.jarkyn.backend.reference.repository.CurrencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    private final SearchFactory searchFactory;

    public CurrencyService(
            CurrencyRepository currencyRepository,
            CurrencyMapper currencyMapper,
            SearchFactory searchFactory
    ) {
        this.currencyRepository = currencyRepository;
        this.currencyMapper = currencyMapper;
        this.searchFactory = searchFactory;
    }

    @Transactional(readOnly = true)
    public CurrencyResponse findApiById(UUID id) {
        CurrencyEntity currency = currencyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return currencyMapper.toResponse(currency);
    }

    @Transactional(readOnly = true)
    public PageResponse<CurrencyResponse> findApiByFilter(QueryParams queryParams) {
        Search<CurrencyResponse> search = searchFactory.createListSearch(
                CurrencyResponse.class, List.of(), () -> currencyMapper.toResponse(currencyRepository.findAll()));
        return search.getResult(queryParams);
    }
}
