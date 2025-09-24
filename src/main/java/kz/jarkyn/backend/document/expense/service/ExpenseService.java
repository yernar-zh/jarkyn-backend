package kz.jarkyn.backend.document.expense.service;


import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.core.service.DocumentTypeService;
import kz.jarkyn.backend.document.expense.mapper.ExpenseMapper;
import kz.jarkyn.backend.document.expense.model.ExpenseEntity;
import kz.jarkyn.backend.document.expense.model.RelatedExpenseEntity;
import kz.jarkyn.backend.document.expense.model.RelatedExpenseEntity_;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseListResponse;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseRequest;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseResponse;
import kz.jarkyn.backend.document.expense.model.dto.RelatedExpenseResponse;
import kz.jarkyn.backend.document.expense.repository.ExpenseRepository;
import kz.jarkyn.backend.operation.service.CashFlowService;
import kz.jarkyn.backend.party.model.AccountEntity;
import kz.jarkyn.backend.party.service.AccountService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final RelatedExpenseService relatedExpenseService;
    private final DocumentService documentService;
    private final AuditService auditService;
    private final AccountService accountService;
    private final CashFlowService cashFlowService;
    private final SearchFactory searchFactory;
    private final DocumentTypeService documentTypeService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseMapper expenseMapper,
            DocumentService documentService,
            AuditService auditService,
            AccountService accountService,
            CashFlowService cashFlowService,
            RelatedExpenseService relatedExpenseService,
            SearchFactory searchFactory, DocumentTypeService documentTypeService) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.relatedExpenseService = relatedExpenseService;
        this.documentService = documentService;
        this.auditService = auditService;
        this.accountService = accountService;
        this.cashFlowService = cashFlowService;
        this.searchFactory = searchFactory;
        this.documentTypeService = documentTypeService;
    }


    @Transactional(readOnly = true)
    public ExpenseResponse findApiById(UUID id) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<RelatedExpenseResponse> relatedExpenses = relatedExpenseService.findResponseByPayment(expense);
        return expenseMapper.toResponse(expense, relatedExpenses);
    }

    @Transactional(readOnly = true)
    public PageResponse<ExpenseListResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<ExpenseEntity> attributes = documentService
                .<ExpenseEntity>generateCriteriaAttributesBuilderFor()
                .add("attachedAmount", (root, query, cb, map) -> {
                    Subquery<BigDecimal> subQuery = query.subquery(BigDecimal.class);
                    Root<RelatedExpenseEntity> relatedExpenseRoot = subQuery.from(RelatedExpenseEntity.class);
                    subQuery.select(cb.coalesce(cb.sum(relatedExpenseRoot.get(RelatedExpenseEntity_.amount)), BigDecimal.ZERO));
                    subQuery.where(cb.equal(relatedExpenseRoot.get(RelatedExpenseEntity_.expense), root));
                    return subQuery;
                })
                .add("notAttachedAmount", (root, query, cb, map) -> cb.diff(
                        (Expression<Number>) map.get("amount"), (Expression<Number>) map.get("attachedAmount")))
                .build();
        Search<ExpenseListResponse> search = searchFactory.createCriteriaSearch(
                ExpenseListResponse.class, List.of("name", "counterparty.name"), QueryParams.Sort.MOMENT_DESC,
                ExpenseEntity.class, attributes);
        return search.getResult(queryParams);
    }

    @Transactional
    public UUID createApi(ExpenseRequest request) {
        ExpenseEntity expense = expenseMapper.toEntity(request);
        expense.setType(documentTypeService.findByCode(DocumentTypeService.DocumentTypeCode.PAYMENT_OUT));
        if (Strings.isBlank(expense.getName())) {
            expense.setName(documentService.findNextName(expense.getType()));
        } else {
            documentService.validateName(expense);
        }
        expense.setDeleted(false);
        expense.setCommited(false);
        expenseRepository.save(expense);
        auditService.saveEntity(expense);
        relatedExpenseService.saveApi(expense, request.getRelatedExpenses());
        return expense.getId();
    }

    @Transactional
    public void editApi(UUID id, ExpenseRequest request) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        documentService.validateName(expense);
        expenseMapper.editEntity(expense, request);
        auditService.saveEntity(expense);
        relatedExpenseService.saveApi(expense, request.getRelatedExpenses());
    }

    @Transactional
    public void commit(UUID id) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        expense.setCommited(Boolean.TRUE);
        auditService.commit(expense);
        AccountEntity account = accountService.findOrCreateForCounterparty(
                expense.getOrganization(), expense.getCounterparty(), expense.getCurrency());
        cashFlowService.create(expense, account, expense.getAmount().negate());
        cashFlowService.create(expense, expense.getAccount(), expense.getAmount().negate());
    }

    @Transactional
    public void undoCommit(UUID id) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        expense.setCommited(Boolean.FALSE);
        auditService.undoCommit(expense);
        cashFlowService.delete(expense);
    }

    @Transactional
    public void delete(UUID id) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (expense.getCommited()) ExceptionUtils.throwCommitedDeleteException();
        expense.setDeleted(Boolean.TRUE);
        auditService.delete(expense);
        relatedExpenseService.saveApi(expense, List.of());
    }
}
