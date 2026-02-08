package kz.jarkyn.backend.document.expense.service;


import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.config.AppRabbitTemplate;
import kz.jarkyn.backend.core.config.RabbitRoutingKeys;
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
import kz.jarkyn.backend.document.core.service.DocumentSearchService;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.core.service.DocumentTypeService;
import kz.jarkyn.backend.document.expense.mapper.ExpenseMapper;
import kz.jarkyn.backend.document.expense.model.ExpenseEntity;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseResponse;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseListResponse;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseRequest;
import kz.jarkyn.backend.document.expense.repository.ExpenseRepository;
import kz.jarkyn.backend.document.expense.model.ExpenseEntity_;
import kz.jarkyn.backend.document.supply.model.dto.SupplyListResponse;
import kz.jarkyn.backend.global.model.CoverageEntity;
import kz.jarkyn.backend.global.model.CoverageEntity_;
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
    private final BindDocumentService bindDocumentService;
    private final DocumentService documentService;
    private final AuditService auditService;
    private final CashFlowService cashFlowService;
    private final DocumentTypeService documentTypeService;
    private final DocumentSearchService documentSearchService;
    private final AppRabbitTemplate appRabbitTemplate;
    private final AccountService accountService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseMapper expenseMapper,
            BindDocumentService bindDocumentService,
            DocumentService documentService,
            AuditService auditService,
            CashFlowService cashFlowService,
            DocumentTypeService documentTypeService,
            DocumentSearchService documentSearchService,
            AppRabbitTemplate appRabbitTemplate, AccountService accountService) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.bindDocumentService = bindDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
        this.cashFlowService = cashFlowService;
        this.documentTypeService = documentTypeService;
        this.documentSearchService = documentSearchService;
        this.appRabbitTemplate = appRabbitTemplate;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public ExpenseResponse findApiById(UUID id) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<BindDocumentResponse> bindDocuments = bindDocumentService.findResponseByPrimaryDocument(expense);
        List<BindDocumentResponse> paidDocuments = bindDocumentService
                .findResponseByRelatedDocument(expense, documentTypeService.findPaymentOut());
        return expenseMapper.toResponse(expense, bindDocuments, paidDocuments);
    }

    @Transactional(readOnly = true)
    public PageResponse<ExpenseListResponse> findApiByFilter(QueryParams queryParams) {
        return documentSearchService.findApiByFilter(ExpenseListResponse.class, queryParams, documentTypeService.findExpense());
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
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, expense.getId());
        return expense.getId();
    }

    @Transactional
    public void editApi(UUID id, ExpenseRequest request) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        documentService.validateName(expense);
        expenseMapper.editEntity(expense, request);
        auditService.saveEntity(expense);
        bindDocumentService.save(expense, request.getBindDocuments());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, expense.getId());
    }

    @Transactional
    public void commit(UUID id) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        expense.setCommited(Boolean.TRUE);
        auditService.commit(expense);

        AccountEntity counterpartyAccount = accountService.findOrCreateForCounterparty(
                expense.getOrganization(), expense.getCounterparty(), expense.getCurrency());
        cashFlowService.create(expense, counterpartyAccount, expense.getAmount());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, expense.getId());
    }

    @Transactional
    public void undoCommit(UUID id) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        expense.setCommited(Boolean.FALSE);
        auditService.undoCommit(expense);
        cashFlowService.delete(expense);
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, expense.getId());
    }

    @Transactional
    public void delete(UUID id) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (expense.getCommited()) ExceptionUtils.throwCommitedDeleteException();
        expense.setDeleted(Boolean.TRUE);
        auditService.delete(expense);
        bindDocumentService.save(expense, List.of());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, expense.getId());
    }
}
