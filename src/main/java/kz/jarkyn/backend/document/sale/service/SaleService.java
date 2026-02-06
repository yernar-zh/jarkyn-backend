
package kz.jarkyn.backend.document.sale.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.config.AppRabbitTemplate;
import kz.jarkyn.backend.core.config.RabbitRoutingKeys;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.core.service.DocumentSearchService;
import kz.jarkyn.backend.document.core.service.DocumentTypeService;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentResponse;
import kz.jarkyn.backend.party.model.*;
import kz.jarkyn.backend.party.service.AccountService;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.core.service.ItemService;
import kz.jarkyn.backend.document.bind.service.BindDocumentService;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.document.sale.model.dto.SaleListResponse;
import kz.jarkyn.backend.document.sale.model.dto.SaleResponse;
import kz.jarkyn.backend.document.sale.model.dto.SaleRequest;
import kz.jarkyn.backend.document.sale.repository.SaleRepository;
import kz.jarkyn.backend.document.sale.mapper.SaleMapper;
import kz.jarkyn.backend.operation.service.CashFlowService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SaleService {
    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final ItemService itemService;
    private final BindDocumentService bindDocumentService;
    private final DocumentService documentService;
    private final AuditService auditService;
    private final CashFlowService cashFlowService;
    private final AccountService accountService;
    private final DocumentTypeService documentTypeService;
    private final DocumentSearchService documentSearchService;
    private final AppRabbitTemplate appRabbitTemplate;

    public SaleService(
            SaleRepository saleRepository,
            SaleMapper saleMapper,
            ItemService itemService,
            BindDocumentService bindDocumentService,
            DocumentService documentService,
            AuditService auditService,
            CashFlowService cashFlowService,
            AccountService accountService,
            DocumentTypeService documentTypeService,
            DocumentSearchService documentSearchService,
            AppRabbitTemplate appRabbitTemplate) {
        this.saleRepository = saleRepository;
        this.saleMapper = saleMapper;
        this.itemService = itemService;
        this.bindDocumentService = bindDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
        this.cashFlowService = cashFlowService;
        this.accountService = accountService;
        this.documentTypeService = documentTypeService;
        this.documentSearchService = documentSearchService;
        this.appRabbitTemplate = appRabbitTemplate;
    }

    @Transactional(readOnly = true)
    public SaleResponse findApiById(UUID id) {
        SaleEntity sale = saleRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<ItemResponse> items = itemService.findApiByDocument(sale);
        List<BindDocumentResponse> paidDocuments = bindDocumentService
                .findResponseByRelatedDocument(sale, documentTypeService.findPaymentIn());
        List<BindDocumentResponse> overheadDocuments = bindDocumentService
                .findResponseByRelatedDocument(sale, documentTypeService.findExpense());
        return saleMapper.toResponse(sale, items, paidDocuments, overheadDocuments);
    }

    @Transactional(readOnly = true)
    public PageResponse<SaleListResponse> findApiByFilter(QueryParams queryParams) {
        return documentSearchService.findApiByFilter(SaleListResponse.class, queryParams, documentTypeService.findSale());
    }

    @Transactional
    public UUID createApi(SaleRequest request) {
        SaleEntity sale = saleMapper.toEntity(request);
        sale.setType(documentTypeService.findSale());
        if (Strings.isBlank(sale.getName())) {
            sale.setName(documentService.findNextName(sale.getType()));
        } else {
            documentService.validateName(sale);
        }
        sale.setDeleted(false);
        sale.setCommited(false);
        saleRepository.save(sale);
        auditService.saveEntity(sale);
        itemService.saveApi(sale, request.getItems());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, sale.getId());
        return sale.getId();
    }

    @Transactional
    public void editApi(UUID id, SaleRequest request) {
        SaleEntity sale = saleRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        documentService.validateName(sale);
        saleMapper.editEntity(sale, request);
        auditService.saveEntity(sale);
        itemService.saveApi(sale, request.getItems());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, sale.getId());
    }

    @Transactional
    public void commit(UUID id) {
        SaleEntity sale = saleRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (sale.getCommited()) return;
        sale.setCommited(Boolean.TRUE);
        auditService.commit(sale);
        itemService.createNegativeTurnover(sale);
        AccountEntity customerAccount = accountService.findOrCreateForCounterparty(
                sale.getOrganization(), sale.getCounterparty(), sale.getCurrency());
        cashFlowService.create(sale, customerAccount, sale.getAmount().negate());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, sale.getId());
    }

    @Transactional
    public void undoCommit(UUID id) {
        SaleEntity sale = saleRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (!sale.getCommited()) return;
        sale.setCommited(Boolean.FALSE);
        auditService.undoCommit(sale);
        itemService.deleteTurnover(sale);
        cashFlowService.delete(sale);
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, sale.getId());
    }

    @Transactional
    public void delete(UUID id) {
        SaleEntity sale = saleRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<BindDocumentResponse> paidDocuments = bindDocumentService
                .findResponseByRelatedDocument(sale, documentTypeService.findPaymentIn());
        if (!paidDocuments.isEmpty()) ExceptionUtils.throwRelationDeleteException();
        if (sale.getCommited()) ExceptionUtils.throwCommitedDeleteException();
        sale.setDeleted(Boolean.TRUE);
        auditService.delete(sale);
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, sale.getId());
    }
}
