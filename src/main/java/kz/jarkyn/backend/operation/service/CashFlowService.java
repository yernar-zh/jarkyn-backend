
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
import java.util.Optional;

@Service
public class CashFlowService {
    private final CashFlowRepository cashFlowRepository;

    public CashFlowService(
            CashFlowRepository cashFlowRepository) {
        this.cashFlowRepository = cashFlowRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal findCurrentBalance(AccountEntity account) {
        return findLastBalanceByAccountAndMoment(account, Instant.now());
    }

    @Transactional(readOnly = true)
    public BigDecimal findLastBalanceByAccountAndMoment(AccountEntity account, Instant moment) {
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
        cashFlow.setBalance(findLastBalanceByAccountAndMoment(account, document.getMoment()));
        cashFlowRepository.save(cashFlow);
        fixBalances(cashFlow.getAccount(), cashFlow.getMoment());
    }

    @Transactional
    public void delete(DocumentEntity document) {
        List<CashFlowEntity> cashFlows = cashFlowRepository.findByDocument(document);
        cashFlowRepository.deleteAll(cashFlows);
        for (CashFlowEntity cashFlow : cashFlows) {
            fixBalances(cashFlow.getAccount(), cashFlow.getMoment());
        }
    }

    @SuppressWarnings("SpringTransactionalMethodCallsInspection")
    private void fixBalances(AccountEntity account, Instant moment) {
        List<CashFlowEntity> cashFlowEntities = cashFlowRepository
                .findByAccountAndMomentGreaterThanEqual(account, moment).stream()
                .sorted(Comparator.comparing(CashFlowEntity::getMoment)
                        .thenComparing(Comparator.comparing(CashFlowEntity::getLastModifiedAt).reversed()))
                .toList();
        if (cashFlowEntities.isEmpty()) return;
        BigDecimal balance = findLastBalanceByAccountAndMoment(account, moment);
        for (CashFlowEntity cashFlowEntity : cashFlowEntities) {
            cashFlowEntity.setBalance(balance);
            balance = balance.add(cashFlowEntity.getAmount());
        }
    }
}
