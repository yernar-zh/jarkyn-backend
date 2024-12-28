
package kz.jarkyn.backend.counterparty.service;


import jakarta.persistence.Tuple;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.counterparty.mapper.SupplierMapper;
import kz.jarkyn.backend.counterparty.model.Currency;
import kz.jarkyn.backend.counterparty.model.SupplierEntity;
import kz.jarkyn.backend.counterparty.model.dto.SupplierListResponse;
import kz.jarkyn.backend.counterparty.model.dto.SupplierRequest;
import kz.jarkyn.backend.counterparty.model.dto.SupplierResponse;
import kz.jarkyn.backend.counterparty.repository.SupplierRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final AuditService auditService;
    private final SearchFactory searchFactory;
    private final AccountService accountService;

    public SupplierService(
            SupplierRepository supplierRepository,
            SupplierMapper supplierMapper,
            AuditService auditService,
            SearchFactory searchFactory,
            AccountService accountService
    ) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public SupplierResponse findApiById(UUID id) {
        SupplierEntity supplier = supplierRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return supplierMapper.toResponse(supplier);
    }

    @Transactional(readOnly = true)
    public PageResponse<SupplierListResponse> findApiByFilter(QueryParams queryParams) {
        Search<SupplierListResponse> search = searchFactory.createListSearch(
                SupplierListResponse.class, List.of("name"),
                () -> supplierRepository.findAll().stream().map(supplier -> {
                    Pair<BigDecimal, Currency> account = accountService.findBalanceByCounterparty(supplier)
                            .stream().findFirst().orElseThrow();
                    Tuple results = supplierRepository.findSupplyInfo(supplier);
                    return supplierMapper.toResponse(supplier, account.getFirst(), account.getSecond(),
                            results.get("firstSupplyMoment", LocalDateTime.class),
                            results.get("lastSupplyMoment", LocalDateTime.class),
                            results.get("totalSupplyCount", Long.class).intValue(),
                            results.get("totalSupplyAmount", BigDecimal.class)
                    );
                }).toList());
        return search.getResult(queryParams);
    }

    @Transactional
    public UUID createApi(SupplierRequest request) {
        SupplierEntity supplier = supplierRepository.save(supplierMapper.toEntity(request));
        auditService.saveChanges(supplier);
        return supplier.getId();
    }

    @Transactional
    public void editApi(UUID id, SupplierRequest request) {
        SupplierEntity supplier = supplierRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        supplierMapper.editEntity(supplier, request);
        auditService.saveChanges(supplier);
    }
}
