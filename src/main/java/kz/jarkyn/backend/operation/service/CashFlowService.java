
package kz.jarkyn.backend.operation.service;


import kz.jarkyn.backend.party.model.AccountEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.operation.mode.CashFlowEntity;
import kz.jarkyn.backend.operation.repository.CashFlowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class CashFlowService {
    private final CashFlowRepository cashFlowRepository;

    public CashFlowService(
            CashFlowRepository cashFlowRepository) {
        this.cashFlowRepository = cashFlowRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal findCurrentBalance(AccountEntity account) {
        return findBalanceByAccountAtMoment(account, Instant.now());
    }

    @Transactional(readOnly = true)
    public BigDecimal findBalanceByAccountAtMoment(AccountEntity account, Instant moment) {
        Optional<CashFlowEntity> cashFlowOpt = cashFlowRepository.findLastByAccountAndMoment(account, moment);
        return cashFlowOpt.map(cashFlow -> cashFlow.getBalance().add(cashFlow.getAmount()))
                .orElse(BigDecimal.ZERO);
    }

    @Transactional
    public void create(DocumentEntity document, AccountEntity account, BigDecimal amount) {
        CashFlowEntity cashFlow = new CashFlowEntity();
        cashFlow.setDocument(document);
        cashFlow.setAccount(account);
        cashFlow.setMoment(document.getMoment());
        cashFlow.setAmount(amount);
        cashFlow.setBalance(BigDecimal.ZERO);
        cashFlowRepository.save(cashFlow);
        fixBalances(cashFlow.getAccount(), cashFlow.getMoment());
    }

    @Transactional
    public void change(DocumentEntity document, AccountEntity account, BigDecimal amount) {
        CashFlowEntity cashFlow = cashFlowRepository.findByDocumentAndAccount(document, account).orElse(null);
        if (cashFlow == null) {
            create(document, account, amount);
            return;
        }

        boolean sameMoment = Objects.equals(cashFlow.getMoment(), document.getMoment());
        boolean sameAmount = (cashFlow.getAmount() == null && amount == null) || (
                cashFlow.getAmount() != null && amount != null && cashFlow.getAmount().compareTo(amount) == 0);
        if (sameMoment && sameAmount) {
            return;
        }

        Instant oldMoment = cashFlow.getMoment();
        cashFlow.setMoment(document.getMoment());
        cashFlow.setAmount(amount);

        Instant fixFromMoment = oldMoment.isBefore(cashFlow.getMoment()) ? oldMoment : cashFlow.getMoment();
        fixBalances(cashFlow.getAccount(), fixFromMoment);
    }

    @Transactional
    public void delete(DocumentEntity document) {
        List<CashFlowEntity> cashFlows = cashFlowRepository.findByDocument(document);
        cashFlowRepository.deleteAll(cashFlows);
        for (CashFlowEntity cashFlow : cashFlows) {
            fixBalances(cashFlow.getAccount(), cashFlow.getMoment());
        }
    }

    @Transactional
    public void deleteAll(DocumentEntity document, Set<AccountEntity> excludeAccounts) {
        List<CashFlowEntity> currentCashFlows = cashFlowRepository.findByDocument(document);
        for (CashFlowEntity cashFlow : currentCashFlows) {
            if (!excludeAccounts.contains(cashFlow.getAccount())) {
                cashFlowRepository.delete(cashFlow);
                fixBalances(cashFlow.getAccount(), cashFlow.getMoment());
            }
        }
    }

    @Transactional
    protected void fixBalances(AccountEntity account, Instant moment) {
        List<CashFlowEntity> cashFlows = cashFlowRepository
                .findByAccountAndMomentGreaterThanEqual(account, moment).stream()
                .sorted(Comparator.comparing(CashFlowEntity::getMoment)
                        .thenComparing(turnoverEntity -> turnoverEntity.getDocument().getLastModifiedAt()))
                .toList();
        BigDecimal balance = findBalanceByAccountAtMoment(account, moment);
        for (CashFlowEntity cashFlow : cashFlows) {
            cashFlow.setBalance(balance);
            balance = balance.add(cashFlow.getAmount());
        }
    }
}
