
package kz.jarkyn.backend.document.supply.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.config.AppRabbitTemplate;
import kz.jarkyn.backend.core.config.RabbitRoutingKeys;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.document.core.service.DocumentSearchService;
import kz.jarkyn.backend.document.core.service.DocumentTypeService;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentResponse;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.operation.model.message.TurnoverFixMessage;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
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
            DocumentSearchService documentSearchService) {
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
    }

    @Transactional(readOnly = true)
    public SupplyResponse findApiById(UUID id) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<ItemResponse> items = itemService.findApiByDocument(supply);
        List<BindDocumentResponse> paidDocuments = bindDocumentService
                .findResponseByRelatedDocument(supply, documentTypeService.findPaymentOut());
        List<BindDocumentResponse> overheadDocuments = bindDocumentService
                .findResponseByRelatedDocument(supply, documentTypeService.findExpense());
        return supplyMapper.toResponse(supply, items, paidDocuments, overheadDocuments);
    }

    @Transactional(readOnly = true)
    public PageResponse<SupplyListResponse> findApiByFilter(QueryParams queryParams) {
        return documentSearchService.findApiByFilter(SupplyListResponse.class, queryParams, documentTypeService.findSupply());
    }

    @Transactional
    public UUID createApi(SupplyRequest request) {
        SupplyEntity supply = supplyMapper.toEntity(request);
        supply.setType(documentTypeService.findSupply());
        if (Strings.isBlank(supply.getName())) {
            supply.setName(documentService.findNextName(supply.getType()));
        } else {
            documentService.validateName(supply);
        }
        supply.setDeleted(false);
        supplyRepository.save(supply);
        save(supply, request.getItems());
        return supply.getId();
    }

    @Transactional
    public void editApi(UUID id, SupplyRequest request) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        documentService.validateName(supply);
        supplyMapper.editEntity(supply, request);
        save(supply, request.getItems());
    }

    private void save(SupplyEntity supply, List<ItemRequest> items) {
        BigDecimal mainCostPrice = supply.getAmount().multiply(supply.getExchangeRate());
        BigDecimal overheadCostPrice = bindDocumentService
                .findResponseByRelatedDocument(supply, documentTypeService.findExpense()).stream()
                .map(bindDocument -> bindDocument.getAmount().multiply(bindDocument.getPrimaryDocument().getExchangeRate()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        itemService.saveApi(supply, items, mainCostPrice.add(overheadCostPrice));

        AccountEntity supplerAccount = accountService.findOrCreateForCounterparty(
                supply.getOrganization(), supply.getCounterparty(), supply.getCurrency());
        if (supply.getCommited()) {
            cashFlowService.change(supply, supplerAccount, supply.getAmount());
            cashFlowService.deleteAll(supply, Set.of(supplerAccount));
        } else {
            cashFlowService.deleteAll(supply, Set.of());
        }

        auditService.saveEntity(supply);
        documentSearchService.sendFixMessage(supply);
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
        documentSearchService.sendFixMessage(supply);
    }
}
