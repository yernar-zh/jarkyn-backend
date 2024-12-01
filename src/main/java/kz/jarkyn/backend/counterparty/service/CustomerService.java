
package kz.jarkyn.backend.counterparty.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.service.CriteriaSearchFactory;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.dto.CustomerListResponse;
import kz.jarkyn.backend.counterparty.model.dto.CustomerRequest;
import kz.jarkyn.backend.counterparty.model.dto.CustomerResponse;
import kz.jarkyn.backend.counterparty.repository.CustomerRepository;
import kz.jarkyn.backend.counterparty.repository.SearchList;
import kz.jarkyn.backend.counterparty.service.mapper.CustomerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AuditService auditService;
    private final CriteriaSearchFactory criteriaSearchFactory;

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper,
            AuditService auditService,
            CriteriaSearchFactory criteriaSearchFactory
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.auditService = auditService;
        this.criteriaSearchFactory = criteriaSearchFactory;
    }


    @Transactional(readOnly = true)
    public CustomerResponse findApiById(UUID id) {
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerListResponse> findApiByFilter(QueryParams queryParams) {
        SearchList<CustomerListResponse> searchResponse = criteriaSearchFactory.createSpecification(
                CustomerListResponse.class, customerRepository.findAllResponse(), "name", "phoneNumber");
        return searchResponse.getResponse(queryParams);
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
