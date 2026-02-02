
package kz.jarkyn.backend.party.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.party.model.dto.AccountShortResponse;
import kz.jarkyn.backend.party.spesification.AccountSpecifications;
import kz.jarkyn.backend.party.spesification.OrganizationSpecifications;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.repository.DocumentRepository;
import kz.jarkyn.backend.document.core.specifications.DocumentSpecifications;
import kz.jarkyn.backend.party.model.*;
import kz.jarkyn.backend.party.model.dto.AccountRequest;
import kz.jarkyn.backend.party.model.dto.AccountResponse;
import kz.jarkyn.backend.party.repository.AccountRepository;
import kz.jarkyn.backend.party.mapper.AccountMapper;
import kz.jarkyn.backend.operation.service.CashFlowService;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import kz.jarkyn.backend.global.service.CurrencyService;
import kz.jarkyn.backend.party.repository.OrganizationRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AuditService auditService;
    private final AccountMapper accountMapper;
    private final SearchFactory searchFactory;
    private final CashFlowService cashFlowService;
    private final CurrencyService currencyService;
    private final DocumentRepository documentRepository;
    private final OrganizationRepository organizationRepository;

    public AccountService(
            AccountRepository accountRepository,
            AuditService auditService,
            AccountMapper accountMapper,
            SearchFactory searchFactory,
            CashFlowService cashFlowService,
            CurrencyService currencyService, DocumentRepository documentRepository, OrganizationRepository organizationRepository) {
        this.accountRepository = accountRepository;
        this.auditService = auditService;
        this.accountMapper = accountMapper;
        this.searchFactory = searchFactory;
        this.cashFlowService = cashFlowService;
        this.currencyService = currencyService;
        this.documentRepository = documentRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional(readOnly = true)
    public AccountResponse findApiById(UUID id) {
        AccountEntity account = accountRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return accountMapper.toResponse(account, cashFlowService.findCurrentBalance(account));
    }

    @Transactional(readOnly = true)
    public PageResponse<AccountResponse> findApiByFilter(QueryParams queryParams) {
        Search<AccountResponse> search = searchFactory.createListSearch(
                AccountResponse.class, List.of("name", "giro"), QueryParams.Sort.NAME_ASC,
                () -> accountRepository.findAll().stream()
                        .filter(account -> account.getCounterparty() == null)
                        .map(account -> accountMapper.toResponse(account, cashFlowService.findCurrentBalance(account)))
                        .toList());
        return search.getResult(queryParams);
    }

    @Transactional
    public AccountEntity findOrCreateForCounterparty(
            OrganizationEntity organization, CounterpartyEntity counterparty, CurrencyEntity currency) {
        return accountRepository.findByOrganizationAndCounterpartyAndCurrency(organization, counterparty, currency)
                .orElseGet(() -> {
                    AccountEntity account = new AccountEntity();
                    account.setOrganization(organization);
                    account.setCounterparty(counterparty);
                    account.setCurrency(currency);
                    account.setName("");
                    account.setBank("");
                    account.setGiro("");
                    account.setArchived(false);
                    accountRepository.save(account);
                    auditService.saveEntity(account);
                    return account;
                });
    }

    @Transactional(readOnly = true)
    public List<AccountShortResponse> findBalanceByCounterparty(CounterpartyEntity counterparty) {
        Stream<AccountShortResponse> accounts = accountRepository
                .findAll(AccountSpecifications.counterparty(counterparty)).stream()
                .sorted(Comparator.comparing(AbstractEntity::getLastModifiedAt).reversed())
                .map(account -> accountMapper.toResponse(account.getOrganization(),
                        cashFlowService.findCurrentBalance(account), account.getCurrency()))
                .filter(balance -> balance.getBalance().compareTo(BigDecimal.ZERO) != 0);
        Stream<AccountShortResponse> defaultAccounts = organizationRepository
                .findAll(OrganizationSpecifications.nonArchived()).stream()
                .map(organization -> accountMapper.toResponse(organization, BigDecimal.ZERO, currencyService.findKZT()));
        return Stream.concat(accounts, defaultAccounts)
                .collect(Collectors.toMap(
                        AccountShortResponse::getOrganization, Function.identity(),
                        (a, b) -> a.getBalance().compareTo(b.getBalance()) >= 0 ? a : b
                )).values().stream().toList();
    }

    @Transactional
    public UUID createApi(AccountRequest request) {
        AccountEntity account = accountRepository.save(accountMapper.toEntity(request));
        account.setCounterparty(null);
        auditService.saveEntity(account);
        return account.getId();
    }

    @Transactional
    public void editApi(UUID id, AccountRequest request) {
        AccountEntity account = accountRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (account.getCounterparty() != null) {
            throw new ApiValidationException("cant update counterparty account");
        }
        ExceptionUtils.requireEqualsApi(account.getOrganization().getId(), request.getOrganization().getId(), "organization");
        ExceptionUtils.requireEqualsApi(account.getCurrency().getId(), request.getCurrency().getId(), "currency");
        accountMapper.editEntity(account, request);
        auditService.saveEntity(account);
    }

    @Transactional
    public AccountResponse archive(UUID id) {
        AccountEntity account = accountRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        account.setArchived(true);
        auditService.archive(account);
        return findApiById(id);
    }

    @Transactional
    public AccountResponse unarchive(UUID id) {
        AccountEntity account = accountRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        account.setArchived(false);
        auditService.unarchive(account);
        return findApiById(id);
    }

    @Transactional
    public void delete(UUID id) {
        AccountEntity account = accountRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        Optional<DocumentEntity> document = documentRepository.findAny(DocumentSpecifications.account(account));
        if (document.isPresent()) ExceptionUtils.throwRelationDeleteException();
        accountRepository.delete(account);
    }
}
