
package kz.jarkyn.backend.document.supply.service;


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
import kz.jarkyn.backend.document.supply.model.SupplyEntity;
import kz.jarkyn.backend.document.supply.model.dto.SupplyListResponse;
import kz.jarkyn.backend.document.supply.model.dto.SupplyResponse;
import kz.jarkyn.backend.document.supply.model.dto.SupplyRequest;
import kz.jarkyn.backend.document.supply.repository.SupplyRepository;
import kz.jarkyn.backend.document.supply.mapper.SupplyMapper;
import kz.jarkyn.backend.operation.service.CashFlowService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SupplyService {
    private final SupplyRepository supplyRepository;
    private final SupplyMapper supplyMapper;
    private final ItemService itemService;
    private final BindDocumentService bindDocumentService;
    private final DocumentService documentService;
    private final AuditService auditService;
    private final CashFlowService cashFlowService;
    private final AccountService accountService;
    private final DocumentTypeService documentTypeService;
    private final DocumentSearchService documentSearchService;
    private final AppRabbitTemplate appRabbitTemplate;

    public SupplyService(
            SupplyRepository supplyRepository,
            SupplyMapper supplyMapper,
            ItemService itemService,
            BindDocumentService bindDocumentService,
            DocumentService documentService,
            AuditService auditService,
            CashFlowService cashFlowService,
            AccountService accountService,
            DocumentTypeService documentTypeService,
            DocumentSearchService documentSearchService, AppRabbitTemplate appRabbitTemplate) {
        this.supplyRepository = supplyRepository;
        this.supplyMapper = supplyMapper;
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
    public SupplyResponse findApiById(UUID id) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<ItemResponse> items = itemService.findApiByDocument(supply);
        List<BindDocumentResponse> paidDocuments = bindDocumentService
                .findResponseByRelatedDocument(supply, documentTypeService.findPaymentOut());
        return supplyMapper.toResponse(supply, items, paidDocuments);
    }

    @Transactional(readOnly = true)
    public PageResponse<SupplyListResponse> findApiByFilter(QueryParams queryParams) {
        return documentSearchService.findApiByFilter(SupplyListResponse.class, queryParams, documentTypeService.findSupply());
    }

    @Transactional
    public UUID createApi(SupplyRequest request) {
        SupplyEntity supply = supplyMapper.toEntity(request);
        supply.setType(documentTypeService.findByCode(DocumentTypeService.DocumentTypeCode.SUPPLY));
        if (Strings.isBlank(supply.getName())) {
            supply.setName(documentService.findNextName(supply.getType()));
        } else {
            documentService.validateName(supply);
        }
        supply.setDeleted(false);
        supply.setCommited(false);
        supplyRepository.save(supply);
        auditService.saveEntity(supply);
        itemService.saveApi(supply, request.getItems());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, supply.getId());
        return supply.getId();
    }

    @Transactional
    public void editApi(UUID id, SupplyRequest request) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        documentService.validateName(supply);
        supplyMapper.editEntity(supply, request);
        auditService.saveEntity(supply);
        itemService.saveApi(supply, request.getItems());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, supply.getId());
    }

    @Transactional
    public void commit(UUID id) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (supply.getCommited()) return;
        supply.setCommited(Boolean.TRUE);
        auditService.commit(supply);
        itemService.createPositiveTurnover(supply, supply.getAmount().multiply(supply.getExchangeRate()));
        AccountEntity supplerAccount = accountService.findOrCreateForCounterparty(
                supply.getOrganization(), supply.getCounterparty(), supply.getCurrency());
        cashFlowService.create(supply, supplerAccount, supply.getAmount());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, supply.getId());
    }

    @Transactional
    public void undoCommit(UUID id) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (!supply.getCommited()) return;
        supply.setCommited(Boolean.FALSE);
        auditService.undoCommit(supply);
        itemService.deleteTurnover(supply);
        cashFlowService.delete(supply);
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, supply.getId());
    }

    @Transactional
    public void delete(UUID id) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<BindDocumentResponse> paidDocuments = bindDocumentService
                .findResponseByRelatedDocument(supply, documentTypeService.findPaymentOut());
        if (!paidDocuments.isEmpty()) ExceptionUtils.throwRelationDeleteException();
        if (supply.getCommited()) ExceptionUtils.throwCommitedDeleteException();
        supply.setDeleted(Boolean.TRUE);
        auditService.delete(supply);
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, supply.getId());
    }
}
