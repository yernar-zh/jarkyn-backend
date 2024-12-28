
package kz.jarkyn.backend.operation.service;


import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.operation.mode.CashFlowEntity;
import kz.jarkyn.backend.operation.repository.CashFlowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CashFlowService {
    private final CashFlowRepository cashFlowRepository;

    public CashFlowService(
            CashFlowRepository cashFlowRepository) {
        this.cashFlowRepository = cashFlowRepository;
    }

    @Transactional
    public void create(DocumentEntity document, AccountEntity account, BigDecimal amount) {
        CashFlowEntity cashFlow = new CashFlowEntity();
        cashFlow.setDocument(document);
        cashFlow.setAccount(account);
        cashFlow.setMoment(document.getMoment());
        cashFlow.setAmount(amount);
        cashFlow.setBalance(findBalance(account));
        cashFlowRepository.save(cashFlow);
    }

    @Transactional(readOnly = true)
    public BigDecimal findBalance(AccountEntity account) {
        Optional<CashFlowEntity> cashFlowOpt = cashFlowRepository.findLastByAccount(account);
        return cashFlowOpt.map(cashFlow -> cashFlow.getBalance().add(cashFlow.getAmount()))
                .orElse(BigDecimal.ZERO);
    }
}
