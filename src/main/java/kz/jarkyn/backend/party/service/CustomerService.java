
package kz.jarkyn.backend.party.service;


import jakarta.persistence.Tuple;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.party.model.CustomerEntity;
import kz.jarkyn.backend.party.model.dto.CustomerListResponse;
import kz.jarkyn.backend.party.model.dto.CustomerRequest;
import kz.jarkyn.backend.party.model.dto.CustomerResponse;
import kz.jarkyn.backend.party.repository.CustomerRepository;
import kz.jarkyn.backend.party.mapper.CustomerMapper;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AuditService auditService;
    private final SearchFactory searchFactory;
    private final AccountService accountService;

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper,
            AuditService auditService,
            SearchFactory searchFactory,
            AccountService accountService
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public CustomerResponse findApiById(UUID id) {
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerListResponse> findApiByFilter(QueryParams queryParams) {
        Search<CustomerListResponse> search = searchFactory.createListSearch(
                CustomerListResponse.class, List.of("name", "phoneNumber"),
                () -> customerRepository.findAll().stream().map(customer -> {
                    Pair<BigDecimal, CurrencyEntity> account = accountService.findBalanceByCounterparty(customer)
                            .stream().findFirst().orElseThrow();
                    Tuple results = customerRepository.findSaleInfo(customer);
                    return customerMapper.toResponse(customer, account.getFirst(), account.getSecond(),
                            results.get("firstSaleMoment", LocalDateTime.class),
                            results.get("lastSaleMoment", LocalDateTime.class),
                            results.get("totalSaleCount", Long.class).intValue(),
                            results.get("totalSaleAmount", BigDecimal.class)
                    );
                }).toList());
        return search.getResult(queryParams);
    }

    @Transactional
    public UUID createApi(CustomerRequest request) {
        CustomerEntity customer = customerRepository.save(customerMapper.toEntity(request));
        auditService.saveChanges(customer);
        return customer.getId();
    }

    @Transactional
    public void editApi(UUID id, CustomerRequest request) {
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        customerMapper.editEntity(customer, request);
        auditService.saveChanges(customer);
    }
}
