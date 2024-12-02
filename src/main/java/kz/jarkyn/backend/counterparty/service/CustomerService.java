
package kz.jarkyn.backend.counterparty.service;


import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.service.*;
import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.counterparty.model.AccountEntity_;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.CustomerEntity_;
import kz.jarkyn.backend.counterparty.model.dto.CustomerListResponse;
import kz.jarkyn.backend.counterparty.model.dto.CustomerRequest;
import kz.jarkyn.backend.counterparty.model.dto.CustomerResponse;
import kz.jarkyn.backend.counterparty.repository.CustomerRepository;
import kz.jarkyn.backend.counterparty.service.mapper.CustomerMapper;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.document.sale.model.SaleEntity_;
import kz.jarkyn.backend.document.sale.model.SaleState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AuditService auditService;
    private final SearchFactory searchFactory;

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper,
            AuditService auditService,
            SearchFactory searchFactory
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
    }


    @Transactional(readOnly = true)
    public CustomerResponse findApiById(UUID id) {
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerListResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<CustomerEntity> attributes = CriteriaAttributes.builder(CustomerEntity.class)
                .add("id", (root, query, cb) -> root.get(CustomerEntity_.id))
                .add("name", (root, query, cb) -> root.get(CustomerEntity_.name))
                .add("shippingAddress", (root, query, cb) -> root.get(CustomerEntity_.shippingAddress))
                .add("discount", (root, query, cb) -> root.get(CustomerEntity_.discount))
                .add("balance", (root, query, cb) -> {
                    Subquery<Integer> subQuery = query.subquery(Integer.class);
                    Root<AccountEntity> accountRoot = subQuery.from(AccountEntity.class);
                    subQuery.select(accountRoot.get(AccountEntity_.balance));
                    subQuery.where(cb.equal(accountRoot.get(AccountEntity_.counterparty), root));
                    return subQuery;
                })
                .add("firstSaleMoment", (root, query, cb) -> {
                    Subquery<LocalDateTime> subQuery = query.subquery(LocalDateTime.class);
                    Root<SaleEntity> saleRoot = subQuery.from(SaleEntity.class);
                    subQuery.select(cb.least(saleRoot.get(SaleEntity_.moment)));
                    subQuery.where(cb.and(
                            cb.equal(saleRoot.get(SaleEntity_.customer), root),
                            cb.equal(saleRoot.get(SaleEntity_.state), SaleState.SHIPPED)
                    ));
                    return subQuery;
                })
                .add("lastSaleMoment", (root, query, cb) -> {
                    Subquery<LocalDateTime> subQuery = query.subquery(LocalDateTime.class);
                    Root<SaleEntity> saleRoot = subQuery.from(SaleEntity.class);
                    subQuery.select(cb.greatest(saleRoot.get(SaleEntity_.moment)));
                    subQuery.where(cb.and(
                            cb.equal(saleRoot.get(SaleEntity_.customer), root),
                            cb.equal(saleRoot.get(SaleEntity_.state), SaleState.SHIPPED)
                    ));
                    return subQuery;
                })
                .add("totalSaleCount", (root, query, cb) -> {
                    Subquery<Integer> subQuery = query.subquery(Integer.class);
                    Root<SaleEntity> saleRoot = subQuery.from(SaleEntity.class);
                    subQuery.select(cb.count(saleRoot).as(Integer.class));
                    subQuery.where(cb.and(
                            cb.equal(saleRoot.get(SaleEntity_.customer), root),
                            cb.equal(saleRoot.get(SaleEntity_.state), SaleState.SHIPPED)
                    ));
                    return subQuery;
                })
                .add("totalSaleAmount", (root, query, cb) -> {
                    Subquery<Integer> subQuery = query.subquery(Integer.class);
                    Root<SaleEntity> saleRoot = subQuery.from(SaleEntity.class);
                    subQuery.select(cb.sum(saleRoot.get(SaleEntity_.amount)));
                    subQuery.where(cb.and(
                            cb.equal(saleRoot.get(SaleEntity_.customer), root),
                            cb.equal(saleRoot.get(SaleEntity_.state), SaleState.SHIPPED)
                    ));
                    return subQuery;
                })
                .build();
        CriteriaSearch<CustomerListResponse, CustomerEntity> search1 = searchFactory.createCriteriaSearch(
                CustomerListResponse.class, List.of("name", "phoneNumber"),
                CustomerEntity.class, attributes);
        PageResponse<CustomerListResponse> result = search1.getResult(queryParams);

        ListSearch<CustomerListResponse> search = searchFactory.createListSearch(
                CustomerListResponse.class, List.of("name", "phoneNumber"),
                customerRepository::findAllResponse);
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
