package kz.jarkyn.backend.document.expense.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.utils.EntityDivider;
import kz.jarkyn.backend.document.expense.mapper.RelatedExpenseMapper;
import kz.jarkyn.backend.document.expense.model.ExpenseEntity;
import kz.jarkyn.backend.document.expense.model.RelatedExpenseEntity;
import kz.jarkyn.backend.document.expense.model.dto.RelatedExpenseRequest;
import kz.jarkyn.backend.document.expense.model.dto.RelatedExpenseResponse;
import kz.jarkyn.backend.document.expense.repository.RelatedExpenseRepository;
import kz.jarkyn.backend.document.expense.specifications.RelatedExpenseSpecifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RelatedExpenseService {
    private final RelatedExpenseRepository relatedExpenseRepository;
    private final RelatedExpenseMapper relatedExpenseMapper;
    private final AuditService auditService;

    public RelatedExpenseService(
            RelatedExpenseRepository relatedExpenseRepository,
            RelatedExpenseMapper relatedExpenseMapper,
            AuditService auditService) {
        this.relatedExpenseRepository = relatedExpenseRepository;
        this.relatedExpenseMapper = relatedExpenseMapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<RelatedExpenseResponse> findResponseByPayment(ExpenseEntity expense) {
        return relatedExpenseRepository.findAll(RelatedExpenseSpecifications.expense(expense))
                .stream().map(relatedExpenseMapper::toResponse).toList();
    }

    @Transactional
    public void saveApi(ExpenseEntity expense, List<RelatedExpenseRequest> relatedExpenseRequests) {
        EntityDivider<RelatedExpenseEntity, RelatedExpenseRequest> divider = new EntityDivider<>(
                relatedExpenseRepository.findAll(RelatedExpenseSpecifications.expense(expense)), relatedExpenseRequests);
        for (EntityDivider<RelatedExpenseEntity, RelatedExpenseRequest>.Entry entry : divider.newReceived()) {
            RelatedExpenseEntity relatedExpense = relatedExpenseMapper.toEntity(entry.getReceived());
            relatedExpense.setExpense(expense);
            relatedExpenseRepository.save(relatedExpense);
            auditService.saveEntity(relatedExpense, relatedExpense.getExpense(), "relatedExpenses");
        }
        for (EntityDivider<RelatedExpenseEntity, RelatedExpenseRequest>.Entry entry : divider.edited()) {
            relatedExpenseMapper.editEntity(entry.getCurrent(), entry.getReceived());
            auditService.saveEntity(entry.getCurrent(), entry.getCurrent().getExpense(), "relatedExpenses");
        }
        for (RelatedExpenseEntity relatedExpense : divider.skippedCurrent()) {
            relatedExpenseRepository.delete(relatedExpense);
            auditService.delete(relatedExpense, relatedExpense.getExpense());
        }
    }
}
