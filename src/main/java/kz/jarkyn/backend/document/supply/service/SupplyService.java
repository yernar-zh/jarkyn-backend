
package kz.jarkyn.backend.document.supply.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.config.RabbitRoutingKeys;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.document.core.model.DocumentSearchEntity;
import kz.jarkyn.backend.document.core.model.DocumentSearchEntity_;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private final SearchFactory searchFactory;
    private final CashFlowService cashFlowService;
    private final AccountService accountService;
    private final DocumentTypeService documentTypeService;
    private final RabbitTemplate rabbitTemplate;

    public SupplyService(
            SupplyRepository supplyRepository,
            SupplyMapper supplyMapper,
            ItemService itemService,
            BindDocumentService bindDocumentService,
            DocumentService documentService,
            AuditService auditService,
            SearchFactory searchFactory,
            CashFlowService cashFlowService,
            AccountService accountService,
            DocumentTypeService documentTypeService,
            RabbitTemplate rabbitTemplate) {
        this.supplyRepository = supplyRepository;
        this.supplyMapper = supplyMapper;
        this.itemService = itemService;
        this.bindDocumentService = bindDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
        this.cashFlowService = cashFlowService;
        this.accountService = accountService;
        this.documentTypeService = documentTypeService;
        this.rabbitTemplate = rabbitTemplate;
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
        CriteriaAttributes<DocumentSearchEntity> attributes = CriteriaAttributes.<DocumentSearchEntity>builder()
                .add("id", (root) -> root.get(DocumentSearchEntity_.id))
                .addEnumType("type", (root) -> root.get(DocumentSearchEntity_.type))
                .add("name", (root) -> root.get(DocumentSearchEntity_.name))
                .addReference("organization", (root) -> root.get(DocumentSearchEntity_.organization))
                .addReference("warehouse", (root) -> root.get(DocumentSearchEntity_.warehouse))
                .addReference("counterparty", (root) -> root.get(DocumentSearchEntity_.counterparty))
                .add("moment", (root) -> root.get(DocumentSearchEntity_.moment))
                .addEnumType("currency", (root) -> root.get(DocumentSearchEntity_.currency))
                .add("exchangeRate", (root) -> root.get(DocumentSearchEntity_.exchangeRate))
                .add("amount", (root) -> root.get(DocumentSearchEntity_.amount))
                .add("deleted", (root) -> root.get(DocumentSearchEntity_.deleted))
                .add("commited", (root) -> root.get(DocumentSearchEntity_.commited))
                .add("comment", (root) -> root.get(DocumentSearchEntity_.comment))
                .add("paidAmount", (root) -> root.get(DocumentSearchEntity_.paidAmount))
                .add("notPaidAmount", (root) -> root.get(DocumentSearchEntity_.notPaidAmount))
                .addEnumType("paidCoverage", (root) -> root.get(DocumentSearchEntity_.paidCoverage))
                .add("discount", (root) -> root.get(DocumentSearchEntity_.discount))
                .add("surcharge", (root) -> root.get(DocumentSearchEntity_.surcharge))
                .add("search", (root) -> root.get(DocumentSearchEntity_.search))
                .build();
        Search<SupplyListResponse> search = searchFactory.createCriteriaSearch(
                SupplyListResponse.class, List.of("name", "counterparty.name"), QueryParams.Sort.MOMENT_DESC,
                DocumentSearchEntity.class, attributes);
        return search.getResult(queryParams);
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
        rabbitTemplate.convertAndSend(RabbitRoutingKeys.SUPPLY_INDEX, supply.getId());
        return supply.getId();
    }

    @Transactional
    public void editApi(UUID id, SupplyRequest request) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        documentService.validateName(supply);
        supplyMapper.editEntity(supply, request);
        auditService.saveEntity(supply);
        rabbitTemplate.convertAndSend(RabbitRoutingKeys.SUPPLY_INDEX, supply.getId());
        itemService.saveApi(supply, request.getItems());
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
    }

    @Transactional
    public void undoCommit(UUID id) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (!supply.getCommited()) return;
        supply.setCommited(Boolean.FALSE);
        auditService.undoCommit(supply);
        itemService.deleteTurnover(supply);
        cashFlowService.delete(supply);
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
    }
}
