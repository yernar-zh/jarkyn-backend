
package kz.jarkyn.backend.counterparty.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.service.CriteriaSearch;
import kz.jarkyn.backend.core.service.CriteriaSearchHolder;
import kz.jarkyn.backend.core.service.CriteriaSearchFactory;
import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.counterparty.model.AccountEntity_;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.CustomerEntity_;
import kz.jarkyn.backend.counterparty.model.dto.CustomerRequest;
import kz.jarkyn.backend.counterparty.model.dto.CustomerSearchResponse;
import kz.jarkyn.backend.counterparty.repository.CustomerRepository;
import kz.jarkyn.backend.counterparty.service.mapper.CustomerMapper;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.document.sale.model.SaleEntity_;
import kz.jarkyn.backend.document.sale.model.SaleState;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AuditService auditService;
    private final ConversionService conversionService;
    private final CriteriaSearchFactory criteriaSearchFactory;


    public CustomerService(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper,
            AuditService auditService,
            EntityManager entityManager,
            ConversionService conversionService,
            CriteriaSearchFactory criteriaSearchFactory
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.auditService = auditService;
        this.criteriaSearchFactory = criteriaSearchFactory;
        this.conversionService = conversionService;
    }

    @Transactional(readOnly = true)
    public CustomerSearchResponse findApiById(UUID id) {
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return customerMapper.toApi(customer);
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerSearchResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaSearch<CustomerSearchResponse> criteriaSearch = criteriaSearchFactory
                .createAsdf(CustomerSearchResponse.class);
        criteriaSearch.specification((cs, query, cb) -> {
            Root<CustomerEntity> customerRoot = query.from(CustomerEntity.class);
            ListJoin<CustomerEntity, AccountEntity> accountJoin = customerRoot
                    .join(CustomerEntity_.accounts, JoinType.LEFT);
            ListJoin<CustomerEntity, SaleEntity> saleJoin = cb.treat(
                    customerRoot.join(CustomerEntity_.documents, JoinType.LEFT), SaleEntity.class);
            cs.attributes()
                    .add(CustomerEntity_.ID, customerRoot.get(CustomerEntity_.id))
                    .add(CustomerEntity_.NAME, customerRoot.get(CustomerEntity_.name))
                    .add(CustomerEntity_.PHONE_NUMBER, customerRoot.get(CustomerEntity_.phoneNumber))
                    .add(CustomerEntity_.SHIPPING_ADDRESS, customerRoot.get(CustomerEntity_.shippingAddress))
                    .add(CustomerEntity_.DISCOUNT, customerRoot.get(CustomerEntity_.discount))
                    .add("balance", accountJoin.get(AccountEntity_.balance))
                    .add("firstSale", cb.least(saleJoin.get(SaleEntity_.moment)))
                    .add("lastSale", cb.greatest(saleJoin.get(SaleEntity_.moment)))
                    .add("totalSaleCount", cb.count(saleJoin).as(Integer.class))
                    .add("totalSaleAmount", cb.sum(saleJoin.get(SaleEntity_.amount)))
                    .finish();
            cs.idAttribute("id");
            cs.searchAttributes("name", "phoneNumber", "shippingAddress");
            cs.groupByAttributes("id", "name", "phoneNumber", "shippingAddress", "discount", "balance");
            cs.where(cb.equal(saleJoin.get(SaleEntity_.state), SaleState.SHIPPED));
        });
        criteriaSearch.queryParams(queryParams);
        return criteriaSearch.getResult();
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
