
package kz.jarkyn.backend.party.service;


import jakarta.persistence.Tuple;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.party.model.CounterpartyEntity;
import kz.jarkyn.backend.party.model.dto.CounterpartyListResponse;
import kz.jarkyn.backend.party.model.dto.CounterpartyRequest;
import kz.jarkyn.backend.party.model.dto.CounterpartyResponse;
import kz.jarkyn.backend.party.repository.CounterpartyRepository;
import kz.jarkyn.backend.party.mapper.CounterpartyMapper;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CounterpartyService {
    private final CounterpartyRepository counterpartyRepository;
    private final CounterpartyMapper counterpartyMapper;
    private final AuditService auditService;
    private final SearchFactory searchFactory;
    private final AccountService accountService;

    public CounterpartyService(
            CounterpartyRepository counterpartyRepository,
            CounterpartyMapper counterpartyMapper,
            AuditService auditService,
            SearchFactory searchFactory,
            AccountService accountService
    ) {
        this.counterpartyRepository = counterpartyRepository;
        this.counterpartyMapper = counterpartyMapper;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public CounterpartyResponse findApiById(UUID id) {
        CounterpartyEntity counterparty = counterpartyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return counterpartyMapper.toResponse(counterparty);
    }

    @Transactional(readOnly = true)
    public PageResponse<CounterpartyListResponse> findApiByFilter(QueryParams queryParams) {
        Search<CounterpartyListResponse> search = searchFactory.createListSearch(
                CounterpartyListResponse.class, List.of("name", "phoneNumber"),
                () -> counterpartyRepository.findAll().stream().map(customer -> {
                    Pair<BigDecimal, CurrencyEntity> account = accountService.findBalanceByCounterparty(customer)
                            .stream().findFirst().orElseThrow();
                    Tuple results = counterpartyRepository.findSaleInfo(customer);
                    return counterpartyMapper.toResponse(customer, account.getFirst(), account.getSecond(),
                            results.get("firstSaleMoment", Instant.class),
                            results.get("lastSaleMoment", Instant.class),
                            results.get("totalSaleCount", Long.class).intValue(),
                            results.get("totalSaleAmount", BigDecimal.class)
                    );
                }).toList());
        return search.getResult(queryParams);
    }

    @Transactional
    public UUID create(CounterpartyRequest request) {
        CounterpartyEntity counterparty = counterpartyRepository.save(counterpartyMapper.toEntity(request));
        auditService.saveChanges(counterparty);
        return counterparty.getId();
    }

    @Transactional
    public void edit(UUID id, CounterpartyRequest request) {
        CounterpartyEntity counterparty = counterpartyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        counterpartyMapper.editEntity(counterparty, request);
        auditService.saveChanges(counterparty);
    }
}
