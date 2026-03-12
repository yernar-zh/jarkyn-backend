package kz.jarkyn.backend.document.expense.service;

import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.config.AppRabbitTemplate;
import kz.jarkyn.backend.core.config.RabbitRoutingKeys;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentRequest;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentResponse;
import kz.jarkyn.backend.document.bind.service.BindDocumentService;
import kz.jarkyn.backend.document.bind.repository.BindDocumentRepository;
import kz.jarkyn.backend.document.bind.specifications.BindDocumentSpecifications;
import kz.jarkyn.backend.document.core.service.DocumentSearchService;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.core.service.DocumentTypeService;
import kz.jarkyn.backend.document.expense.mapper.ExpenseMapper;
import kz.jarkyn.backend.document.expense.model.ExpenseEntity;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseListResponse;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseRequest;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseResponse;
import kz.jarkyn.backend.document.expense.repository.ExpenseRepository;
import kz.jarkyn.backend.document.supply.service.SupplyService;
import kz.jarkyn.backend.operation.service.CashFlowService;
import kz.jarkyn.backend.party.model.AccountEntity;
import kz.jarkyn.backend.party.service.AccountService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
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
    private final BindDocumentRepository bindDocumentRepository;
    private final SupplyService supplyService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseMapper expenseMapper,
            BindDocumentService bindDocumentService,
            DocumentService documentService,
            AuditService auditService,
            CashFlowService cashFlowService,
            DocumentTypeService documentTypeService,
            DocumentSearchService documentSearchService,
            AppRabbitTemplate appRabbitTemplate,
            AccountService accountService,
            BindDocumentRepository bindDocumentRepository,
            SupplyService supplyService) {
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
        this.bindDocumentRepository = bindDocumentRepository;
        this.supplyService = supplyService;
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
        return documentSearchService.findApiByFilter(
                ExpenseListResponse.class, queryParams, documentTypeService.findExpense());
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
        expenseRepository.save(expense);
        save(expense, request.getBindDocuments());
        supplyService.recalculateCostForSupplies(findRelatedSupplyIds(expense));
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, expense.getId());
        return expense.getId();
    }

    @Transactional
    public void editApi(UUID id, ExpenseRequest request) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        Set<UUID> affectedSupplyIds = new HashSet<>(findRelatedSupplyIds(expense));
        documentService.validateName(expense);
        expenseMapper.editEntity(expense, request);
        save(expense, request.getBindDocuments());
        affectedSupplyIds.addAll(findRelatedSupplyIds(expense));
        supplyService.recalculateCostForSupplies(affectedSupplyIds);
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, expense.getId());
    }

    private void save(ExpenseEntity expense, List<BindDocumentRequest> bindDocumentRequests) {
        auditService.saveEntity(expense);
        bindDocumentService.save(expense, bindDocumentRequests);

        AccountEntity counterpartyAccount = accountService.findOrCreateForCounterparty(
                expense.getOrganization(), expense.getCounterparty(), expense.getCurrency());
        if (Boolean.TRUE.equals(expense.getCommited())) {
            cashFlowService.change(expense, counterpartyAccount, expense.getAmount());
            cashFlowService.deleteAll(expense, Set.of(counterpartyAccount));
        } else {
            cashFlowService.deleteAll(expense, Set.of());
        }
    }

    @Transactional
    public void delete(UUID id) {
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (expense.getDeleted()) return;
        Set<UUID> affectedSupplyIds = findRelatedSupplyIds(expense);
        expense.setCommited(Boolean.FALSE);
        expense.setDeleted(Boolean.TRUE);
        auditService.delete(expense);
        cashFlowService.deleteAll(expense, Set.of());
        bindDocumentService.save(expense, List.of());
        supplyService.recalculateCostForSupplies(affectedSupplyIds);
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, expense.getId());
    }

    private Set<UUID> findRelatedSupplyIds(ExpenseEntity expense) {
        return bindDocumentRepository.findAll(BindDocumentSpecifications.primaryDocument(expense)).stream()
                .map(bindDocument -> bindDocument.getRelatedDocument())
                .filter(relatedDocument -> documentTypeService.isSupply(relatedDocument.getType()))
                .map(relatedDocument -> relatedDocument.getId())
                .collect(java.util.stream.Collectors.toSet());
    }
}
