
package kz.jarkyn.backend.document.supply.service;


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
import kz.jarkyn.backend.document.core.service.DocumentTypeService;
import kz.jarkyn.backend.document.payment.model.PaymentOutEntity_;
import kz.jarkyn.backend.party.model.*;
import kz.jarkyn.backend.party.service.AccountService;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.core.service.ItemService;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity_;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.payment.service.PaidDocumentService;
import kz.jarkyn.backend.document.supply.model.SupplyEntity;
import kz.jarkyn.backend.document.supply.model.SupplyEntity_;
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
import java.util.List;
import java.util.UUID;

@Service
public class SupplyService {
    private final SupplyRepository supplyRepository;
    private final SupplyMapper supplyMapper;
    private final ItemService itemService;
    private final PaidDocumentService paidDocumentService;
    private final DocumentService documentService;
    private final AuditService auditService;
    private final SearchFactory searchFactory;
    private final CashFlowService cashFlowService;
    private final AccountService accountService;
    private final DocumentTypeService documentTypeService;

    public SupplyService(
            SupplyRepository supplyRepository,
            SupplyMapper supplyMapper,
            ItemService itemService,
            PaidDocumentService paidDocumentService,
            DocumentService documentService,
            AuditService auditService,
            SearchFactory searchFactory,
            CashFlowService cashFlowService,
            AccountService accountService, DocumentTypeService documentTypeService) {
        this.supplyRepository = supplyRepository;
        this.supplyMapper = supplyMapper;
        this.itemService = itemService;
        this.paidDocumentService = paidDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
        this.cashFlowService = cashFlowService;
        this.accountService = accountService;
        this.documentTypeService = documentTypeService;
    }

    @Transactional(readOnly = true)
    public SupplyResponse findApiById(UUID id) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<ItemResponse> items = itemService.findApiByDocument(supply);
        List<PaidDocumentResponse> paidDocuments = paidDocumentService.findResponseByDocument(supply);
        return supplyMapper.toResponse(supply, items, paidDocuments);
    }

    @Transactional(readOnly = true)
    public PageResponse<SupplyListResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<SupplyEntity> attributes = CriteriaAttributes.<SupplyEntity>builder()
                .add("id", (root) -> root.get(SupplyEntity_.id))
                .addEnumType("type", (root) -> root.get(PaymentOutEntity_.type))
                .add("name", (root) -> root.get(SupplyEntity_.name))
                .addReference("organization", (root) -> root.get(SupplyEntity_.organization))
                .add("moment", (root) -> root.get(SupplyEntity_.moment))
                .addEnumType("currency", (root) -> root.get(SupplyEntity_.currency))
                .add("exchangeRate", (root) -> root.get(SupplyEntity_.exchangeRate))
                .add("amount", (root) -> root.get(SupplyEntity_.amount))
                .add("deleted", (root) -> root.get(SupplyEntity_.deleted))
                .add("commited", (root) -> root.get(SupplyEntity_.commited))
                .add("comment", (root) -> root.get(SupplyEntity_.comment))
                .addReference("warehouse", (root) -> root.get(SupplyEntity_.warehouse))
                .addReference("counterparty", (root) -> root.get(SupplyEntity_.counterparty))
                .add("paidAmount", (root, query, cb, map) -> {
                    Subquery<BigDecimal> subQuery = query.subquery(BigDecimal.class);
                    Root<PaidDocumentEntity> paidDocumentRoot = subQuery.from(PaidDocumentEntity.class);
                    subQuery.select(cb.coalesce(cb.sum(paidDocumentRoot.get(PaidDocumentEntity_.amount)), BigDecimal.ZERO));
                    subQuery.where(cb.equal(paidDocumentRoot.get(PaidDocumentEntity_.document), root));
                    return subQuery;
                })
                .add("notPaidAmount", (root, query, cb, map) -> cb.diff(
                        (Expression<Number>) map.get("amount"), (Expression<Number>) map.get("paidAmount")))
                .build();
        Search<SupplyListResponse> search = searchFactory.createCriteriaSearch(
                SupplyListResponse.class, List.of("name", "counterparty.name"),
                SupplyEntity.class, attributes);
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
        auditService.saveChanges(supply);
        itemService.saveApi(supply, request.getItems());
        return supply.getId();
    }

    @Transactional
    public void editApi(UUID id, SupplyRequest request) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        documentService.validateName(supply);
        supplyMapper.editEntity(supply, request);
        auditService.saveChanges(supply);
        itemService.saveApi(supply, request.getItems());
    }

    @Transactional
    public void commit(UUID id) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (supply.getCommited()) return;
        supply.setCommited(Boolean.TRUE);
        auditService.saveChanges(supply);
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
        auditService.saveChanges(supply);
        itemService.deleteTurnover(supply);
        cashFlowService.delete(supply);
    }

    @Transactional
    public void delete(UUID id) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<PaidDocumentResponse> paidDocuments = paidDocumentService.findResponseByDocument(supply);
        if (!paidDocuments.isEmpty()) ExceptionUtils.throwRelationDeleteException();
        if (supply.getCommited()) ExceptionUtils.throwCommitedDeleteException();
        supply.setDeleted(Boolean.TRUE);
        auditService.saveChanges(supply);
    }
}
