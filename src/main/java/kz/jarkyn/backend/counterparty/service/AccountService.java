
package kz.jarkyn.backend.counterparty.service;


import jakarta.persistence.criteria.JoinType;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.counterparty.model.*;
import kz.jarkyn.backend.counterparty.model.dto.AccountRequest;
import kz.jarkyn.backend.counterparty.model.dto.AccountResponse;
import kz.jarkyn.backend.counterparty.repository.AccountRepository;
import kz.jarkyn.backend.counterparty.mapper.AccountMapper;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.good.model.SellingPriceEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AuditService auditService;
    private final AccountMapper accountMapper;
    private final SearchFactory searchFactory;

    public AccountService(
            AccountRepository accountRepository,
            AuditService auditService,
            AccountMapper accountMapper, SearchFactory searchFactory) {
        this.accountRepository = accountRepository;
        this.auditService = auditService;
        this.accountMapper = accountMapper;
        this.searchFactory = searchFactory;
    }

    @Transactional(readOnly = true)
    public AccountResponse findApiById(UUID id) {
        AccountEntity account = accountRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return accountMapper.toResponse(account);
    }

    @Transactional(readOnly = true)
    public PageResponse<AccountResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<AccountEntity> attributes = CriteriaAttributes.<AccountEntity>builder()
                .add("id", (root) -> root.get(AccountEntity_.id))
                .add("name", (root) -> root.get(AccountEntity_.name))
                .add("organization.id", (root) -> root
                        .get(AccountEntity_.organization).get(OrganizationEntity_.id))
                .add("organization.name", (root) -> root
                        .get(AccountEntity_.organization).get(OrganizationEntity_.name))
                .add("counterparty.id", (root) -> root
                        .join(AccountEntity_.counterparty, JoinType.LEFT).get(CounterpartyEntity_.id))
                .add("counterparty.name", (root) -> root
                        .join(AccountEntity_.counterparty, JoinType.LEFT).get(CounterpartyEntity_.name))
                .add("bank", (root) -> root.get(AccountEntity_.bank))
                .add("giro", (root) -> root.get(AccountEntity_.giro))
                .add("currency", (root) -> root.get(AccountEntity_.currency))
                .add("balance", (root) -> root.get(AccountEntity_.balance))
                .build();
        Search<AccountResponse> search = searchFactory.createCriteriaSearch(
                AccountResponse.class, List.of("name", "giro"),
                AccountEntity.class, attributes);
        return search.getResult(queryParams);
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
    public UUID createApi(AccountRequest request) {
        AccountEntity account = accountRepository.save(accountMapper.toEntity(request));
        account.setCounterparty(null);
        account.setBalance(BigDecimal.ZERO);
        auditService.saveChanges(account);
        return account.getId();
    }

    @Transactional
    public void editApi(UUID id, AccountRequest request) {
        AccountEntity account = accountRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (account.getCounterparty() != null) {
            throw new ApiValidationException("cant update counterparty account");
        }
        ExceptionUtils.requireEqualsApi(account.getCurrency(), request.getCurrency(), "currency");
        accountMapper.editEntity(account, request);
        auditService.saveChanges(account);
    }
}
