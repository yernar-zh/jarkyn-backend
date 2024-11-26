
package kz.jarkyn.backend.counterparty.service;


import jakarta.persistence.EntityManager;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.CoreSpecifications;
import kz.jarkyn.backend.core.PageSpecification;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.service.CriteriaSearchFactory;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.CustomerEntity_;
import kz.jarkyn.backend.counterparty.model.dto.CustomerRequest;
import kz.jarkyn.backend.counterparty.model.dto.CustomerSearchResponse;
import kz.jarkyn.backend.counterparty.repository.CustomerRepository;
import kz.jarkyn.backend.counterparty.service.mapper.CustomerMapper;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AuditService auditService;
    private final ConversionService conversionService;
    private final CriteriaSearchFactory pageSpesificationFactory;


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
        this.pageSpesificationFactory = criteriaSearchFactory;
        this.conversionService = conversionService;
    }

    @Transactional(readOnly = true)
    public CustomerSearchResponse findApiById(UUID id) {
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return customerMapper.toApi(customer);
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerSearchResponse> findApiByFilter(QueryParams queryParams) {
        PageSpecification<CustomerEntity> pageSpecification = pageSpesificationFactory
                .createSpecification(CustomerEntity.class, queryParams)
                .filterAndSort("id", CustomerEntity_.id)
                .filterAndSort("name", CustomerEntity_.name)
                .filterAndSort("phoneNumber", CustomerEntity_.phoneNumber)
                .filterAndSort("shippingAddress", CustomerEntity_.shippingAddress)
                .filterAndSort("discount", CustomerEntity_.discount);
        Page<CustomerEntity> result = customerRepository.findAll(pageSpecification);
        return null;
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
