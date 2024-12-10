
package kz.jarkyn.backend.counterparty.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.DataValidationException;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.OrganizationEntity;
import kz.jarkyn.backend.counterparty.model.dto.AccountRequest;
import kz.jarkyn.backend.counterparty.model.dto.AccountResponse;
import kz.jarkyn.backend.counterparty.repository.AccountRepository;
import kz.jarkyn.backend.counterparty.mapper.AccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kz.jarkyn.backend.core.exception.ExceptionUtils.ENTITY_NOT_FOUND;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AuditService auditService;
    private final OrganizationService organizationService;
    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository, AuditService auditService, OrganizationService organizationService, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.auditService = auditService;
        this.organizationService = organizationService;
        this.accountMapper = accountMapper;
    }

    @Transactional(readOnly = true)
    public AccountResponse findApiById(UUID id) {
        OrganizationEntity curOrganization = organizationService.getCurrent();
        AccountEntity account = accountRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (!account.getCounterparty().equals(curOrganization)) {
            throw new DataValidationException(ENTITY_NOT_FOUND, "entity not found");
        }
        return accountMapper.toResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findApiAll() {
        OrganizationEntity curOrganization = organizationService.getCurrent();
        List<AccountEntity> entities = accountRepository.findByCounterparty(curOrganization);
        return accountMapper.toResponse(entities);
    }

    @Transactional
    public UUID createApi(AccountRequest request) {
        OrganizationEntity curOrganization = organizationService.getCurrent();
        AccountEntity account = accountRepository.save(accountMapper.toEntity(request));
        account.setCounterparty(curOrganization);
        account.setBalance(BigDecimal.ZERO);
        auditService.saveChanges(account);
        return account.getId();
    }

    @Transactional
    public void createForCustomer(CustomerEntity customer) {
        AccountEntity account = new AccountEntity();
        account.setCounterparty(customer);
        account.setBalance(BigDecimal.ZERO);
        accountRepository.save(account);
        auditService.saveChanges(account);
    }

    @Transactional
    public void editApi(UUID id, AccountRequest request) {
        AccountEntity account = accountRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        accountMapper.editEntity(account, request);
        auditService.saveChanges(account);
    }
}
