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
import kz.jarkyn.backend.document.bind.model.BindDocumentEntity;
import kz.jarkyn.backend.document.bind.model.BindDocumentEntity_;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentResponse;
import kz.jarkyn.backend.document.bind.service.BindDocumentService;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.core.service.DocumentTypeService;
import kz.jarkyn.backend.document.expense.mapper.ExpenseMapper;
import kz.jarkyn.backend.document.expense.model.ExpenseEntity;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseResponse;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseListResponse;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseRequest;
import kz.jarkyn.backend.document.expense.repository.ExpenseRepository;
import kz.jarkyn.backend.document.expense.model.ExpenseEntity_;
import kz.jarkyn.backend.global.model.CoverageEntity;
import kz.jarkyn.backend.global.model.CoverageEntity_;
import kz.jarkyn.backend.operation.service.CashFlowService;
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
    private final BindDocumentService bindDocumentService;
    private final DocumentService documentService;
    private final AuditService auditService;
    private final SearchFactory searchFactory;
    private final CashFlowService cashFlowService;
    private final DocumentTypeService documentTypeService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseMapper expenseMapper,
            BindDocumentService bindDocumentService,
            DocumentService documentService,
            AuditService auditService,
            SearchFactory searchFactory,
            CashFlowService cashFlowService,
            DocumentTypeService documentTypeService
    ) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.bindDocumentService = bindDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
        this.cashFlowService = cashFlowService;
        this.documentTypeService = documentTypeService;
    }

    @Transactional(readOnly = true)
    public ExpenseResponse findApiById(UUID id) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<BindDocumentResponse> bindDocuments = bindDocumentService.findResponseByPrimaryDocument(expense);
        return expenseMapper.toResponse(expense, bindDocuments);
    }

    @Transactional(readOnly = true)
    public PageResponse<ExpenseListResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<ExpenseEntity> attributes = documentService
                .<ExpenseEntity>generateCriteriaAttributesBuilderFor()
                .add("receiptNumber", (root) -> root.get(ExpenseEntity_.receiptNumber))
                .addEnumType("itemOfExpenditure", (root) -> root.get(ExpenseEntity_.itemOfExpenditure))
                .add("purpose", (root) -> root.get(ExpenseEntity_.purpose))
                .add("attachedAmount", (root, query, cb, map) -> {
                    Subquery<BigDecimal> subQuery = query.subquery(BigDecimal.class);
                    Root<BindDocumentEntity> bindDocumentRoot = subQuery.from(BindDocumentEntity.class);
                    subQuery.select(cb.coalesce(cb.sum(bindDocumentRoot.get(BindDocumentEntity_.amount)), BigDecimal.ZERO));
                    subQuery.where(cb.equal(bindDocumentRoot.get(BindDocumentEntity_.primaryDocument), root));
                    return subQuery;
                })
                .add("attachedCoverage.id", (root, query, cb, map) -> {
                    Expression<Number> amount = (Expression<Number>) map.get("amount");
                    Expression<Number> attachedAmount = (Expression<Number>) map.get("attachedAmount");
                    Expression<String> coverageCode = cb.<String>selectCase()
                            .when(cb.equal(amount, attachedAmount), CoverageEntity.FULL)
                            .when(cb.equal(attachedAmount, 0), CoverageEntity.NONE)
                            .otherwise(CoverageEntity.PARTIAL);
                    Subquery<UUID> subQuery = query.subquery(UUID.class);
                    Root<CoverageEntity> coverageRoot = subQuery.from(CoverageEntity.class);
                    return subQuery.select(coverageRoot.get(CoverageEntity_.id))
                            .where(cb.equal(coverageRoot.get(CoverageEntity_.code), coverageCode));
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
        expense.setType(documentTypeService.findExpense());
        if (Strings.isBlank(expense.getName())) {
            expense.setName(documentService.findNextName(expense.getType()));
        } else {
            documentService.validateName(expense);
        }
        expense.setDeleted(false);
        expense.setCommited(false);
        expenseRepository.save(expense);
        auditService.saveEntity(expense);
        bindDocumentService.save(expense, request.getBindDocuments());
        return expense.getId();
    }

    @Transactional
    public void editApi(UUID id, ExpenseRequest request) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        documentService.validateName(expense);
        expenseMapper.editEntity(expense, request);
        auditService.saveEntity(expense);
        bindDocumentService.save(expense, request.getBindDocuments());
    }

    @Transactional
    public void commit(UUID id) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        expense.setCommited(Boolean.TRUE);
        auditService.commit(expense);
        cashFlowService.create(expense, expense.getAccount(), expense.getAmount());
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
        bindDocumentService.save(expense, List.of());
    }
}
