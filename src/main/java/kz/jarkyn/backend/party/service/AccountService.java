
package kz.jarkyn.backend.party.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.party.model.*;
import kz.jarkyn.backend.party.model.dto.AccountRequest;
import kz.jarkyn.backend.party.model.dto.AccountResponse;
import kz.jarkyn.backend.party.repository.AccountRepository;
import kz.jarkyn.backend.party.mapper.AccountMapper;
import kz.jarkyn.backend.operation.service.CashFlowService;
import kz.jarkyn.backend.reference.model.CurrencyEntity;
import kz.jarkyn.backend.reference.service.CurrencyService;
import org.springframework.data.util.Pair;
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
    private final CashFlowService cashFlowService;
    private final CurrencyService currencyService;

    public AccountService(
            AccountRepository accountRepository,
            AuditService auditService,
            AccountMapper accountMapper,
            SearchFactory searchFactory,
            CashFlowService cashFlowService,
            CurrencyService currencyService) {
        this.accountRepository = accountRepository;
        this.auditService = auditService;
        this.accountMapper = accountMapper;
        this.searchFactory = searchFactory;
        this.cashFlowService = cashFlowService;
        this.currencyService = currencyService;
    }

    @Transactional(readOnly = true)
    public AccountResponse findApiById(UUID id) {
        AccountEntity account = accountRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return accountMapper.toResponse(account, cashFlowService.findBalance(account));
    }

    @Transactional(readOnly = true)
    public PageResponse<AccountResponse> findApiByFilter(QueryParams queryParams) {
        Search<AccountResponse> search = searchFactory.createListSearch(
                AccountResponse.class, List.of("name", "giro"),
                () -> accountRepository.findAll().stream()
                        .map(account -> accountMapper.toResponse(account, cashFlowService.findBalance(account)))
                        .toList());
        return search.getResult(queryParams);
    }

    @Transactional
    public AccountEntity findOrCreateForCounterparty(
            OrganizationEntity organization, PartyEntity counterparty, CurrencyEntity currency) {
        return accountRepository.findByOrganizationAndCounterpartyAndCurrency(organization, counterparty, currency)
                .orElseGet(() -> {
                    AccountEntity account = new AccountEntity();
                    account.setOrganization(organization);
                    account.setCounterparty(counterparty);
                    account.setCurrency(currency);
                    account.setName("");
                    account.setBank("");
                    account.setGiro("");
                    accountRepository.save(account);
                    auditService.saveChanges(account);
                    return account;
                });
    }

    @Transactional(readOnly = true)
    public List<Pair<BigDecimal, CurrencyEntity>> findBalanceByCounterparty(PartyEntity counterparty) {
        List<Pair<BigDecimal, CurrencyEntity>> result = accountRepository.findByCounterparty(counterparty).stream()
                .sorted(Comparator.comparing(AbstractEntity::getLastModifiedAt).reversed())
                .map(account -> Pair.of(cashFlowService.findBalance(account), account.getCurrency()))
                .filter(pair -> pair.getFirst().compareTo(BigDecimal.ZERO) != 0)
                .toList();
        if (!result.isEmpty()) {
            return result;
        }
        return List.of(Pair.of(BigDecimal.ZERO, currencyService.findKZT()));
    }

    @Transactional
    public UUID createApi(AccountRequest request) {
        AccountEntity account = accountRepository.save(accountMapper.toEntity(request));
        account.setCounterparty(null);
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
