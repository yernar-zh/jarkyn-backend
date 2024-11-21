
package kz.jarkyn.backend.counterparty.service;


import jakarta.persistence.Tuple;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.utils.PrefixSearch;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.dto.CustomerDto;
import kz.jarkyn.backend.counterparty.model.dto.CustomerRequest;
import kz.jarkyn.backend.counterparty.model.dto.CustomerResponse;
import kz.jarkyn.backend.counterparty.model.filter.CustomerRequestQuery;
import kz.jarkyn.backend.counterparty.repository.AccountRepository;
import kz.jarkyn.backend.counterparty.repository.CustomerRepository;
import kz.jarkyn.backend.counterparty.service.mapper.CustomerMapper;
import kz.jarkyn.backend.document.sale.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final SaleRepository saleRepository;
    private final CustomerMapper customerMapper;
    private final AuditService auditService;

    public CustomerService(
            CustomerRepository customerRepository,
            AccountRepository accountRepository,
            SaleRepository saleRepository,
            CustomerMapper customerMapper,
            AuditService auditService
    ) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.saleRepository = saleRepository;
        this.customerMapper = customerMapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public CustomerResponse findApiById(UUID id) {
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return customerMapper.toApi(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findApiByFilter(CustomerRequestQuery requestQuery) {
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

    private List<CustomerDto> findAllDto() {
        List<CustomerDto> result = new ArrayList<>();
        return customerRepository.findDtoAll();
    }
}
