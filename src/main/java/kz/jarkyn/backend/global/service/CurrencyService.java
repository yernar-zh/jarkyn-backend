
package kz.jarkyn.backend.global.service;


import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.global.mapper.CurrencyMapper;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import kz.jarkyn.backend.global.model.CurrencyEntity_;
import kz.jarkyn.backend.global.model.dto.CurrencyResponse;
import kz.jarkyn.backend.global.repository.CurrencyRepository;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity_;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseResponse;
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
        CriteriaAttributes<CurrencyEntity> attributes = CriteriaAttributes.<CurrencyEntity>builder()
                .add("id", (root) -> root.get(CurrencyEntity_.id))
                .add("name", (root) -> root.get(CurrencyEntity_.name))
                .add("archived", (root) -> root.get(CurrencyEntity_.archived))
                .build();
        Search<CurrencyResponse> search = searchFactory.createCriteriaSearch(
                CurrencyResponse.class, List.of("name"),
                CurrencyEntity.class, attributes);
        return search.getResult(queryParams);
    }

    @Transactional(readOnly = true)
    public CurrencyEntity findKZT() {
        return currencyRepository.findAll().stream()
                .filter(currency -> currency.getName().equals("KZT")).findFirst().orElseThrow();
    }
}
