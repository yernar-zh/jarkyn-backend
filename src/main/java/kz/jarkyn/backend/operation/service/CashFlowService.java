
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
    public void delete(DocumentEntity document) {
        List<CashFlowEntity> cashFlows = cashFlowRepository.findByDocument(document);
        cashFlowRepository.deleteAll(cashFlows);
        for (CashFlowEntity cashFlow : cashFlows) {
            fixBalances(cashFlow.getAccount(), cashFlow.getMoment());
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
