
package kz.jarkyn.backend.counterparty.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.DataValidationException;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.counterparty.model.*;
import kz.jarkyn.backend.counterparty.model.dto.AccountRequest;
import kz.jarkyn.backend.counterparty.model.dto.AccountResponse;
import kz.jarkyn.backend.counterparty.repository.AccountRepository;
import kz.jarkyn.backend.counterparty.mapper.AccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
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
        return null;
    }

    @Transactional
    public UUID createApi(AccountRequest request) {
        AccountEntity account = accountRepository.save(accountMapper.toEntity(request));
        account.setCounterparty(null);
        account.setBalance(BigDecimal.ZERO);
        auditService.saveChanges(account);
        return account.getId();
    }

    @Transactional
    public AccountEntity findOrCreateForCounterparty(
            OrganizationEntity organization, CounterpartyEntity counterparty, Currency currency) {
        return accountRepository.findByOrganizationAndCounterpartyAndCurrency(organization, counterparty, currency)
                .orElseGet(() -> {
                    AccountEntity account = new AccountEntity();
                    account.setOrganization(organization);
                    account.setCounterparty(counterparty);
                    account.setCurrency(currency);
                    account.setBalance(BigDecimal.ZERO);
                    accountRepository.save(account);
                    auditService.saveChanges(account);
                    return account;
                });
    }

    @Transactional(readOnly = true)
    public List<AccountEntity> findByCounterparty(CounterpartyEntity counterparty) {
        return accountRepository.findByCounterparty(counterparty).stream()
                .filter(account -> account.getBalance().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(AbstractEntity::getLastModifiedAt).reversed())
                .toList();
    }

    @Transactional
    public void editApi(UUID id, AccountRequest request) {
        AccountEntity account = accountRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        ExceptionUtils.requireEqualsApi(account.getCurrency(), request.getCurrency(), "currency");
        accountMapper.editEntity(account, request);
        auditService.saveChanges(account);
    }
}
