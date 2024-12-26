
package kz.jarkyn.backend.document.supply.service;


import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.counterparty.model.*;
import kz.jarkyn.backend.counterparty.model.dto.AccountResponse;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity_;
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

    public SupplyService(
            SupplyRepository supplyRepository,
            SupplyMapper supplyMapper,
            ItemService itemService,
            PaidDocumentService paidDocumentService,
            DocumentService documentService,
            AuditService auditService,
            SearchFactory searchFactory
    ) {
        this.supplyRepository = supplyRepository;
        this.supplyMapper = supplyMapper;
        this.itemService = itemService;
        this.paidDocumentService = paidDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
    }

    @Transactional(readOnly = true)
    public SupplyResponse findApiById(UUID id) {
        SupplyEntity supply = supplyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<ItemResponse> items = itemService.findApiByDocument(supply);
        List<PaidDocumentResponse> paidDocuments = paidDocumentService.findResponseByDocument(supply);
        return supplyMapper.toDetailResponse(supply, items, paidDocuments);
    }

    @Transactional(readOnly = true)
    public PageResponse<SupplyListResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<SupplyEntity> attributes = CriteriaAttributes.<SupplyEntity>builder()
                .add("id", (root, query, cb) -> root.get(SupplyEntity_.id))
                .add("name", (root, query, cb) -> root.get(SupplyEntity_.name))
                .add("organization.id", (root, query, cb) -> root
                        .get(SupplyEntity_.organization).get(OrganizationEntity_.id))
                .add("organization.name", (root, query, cb) -> root
                        .get(SupplyEntity_.organization).get(OrganizationEntity_.name))
                .add("moment", (root, query, cb) -> root.get(SupplyEntity_.moment))
                .add("currency", (root, query, cb) -> root.get(SupplyEntity_.currency))
                .add("exchangeRate", (root, query, cb) -> root.get(SupplyEntity_.exchangeRate))
                .add("amount", (root, query, cb) -> root.get(SupplyEntity_.amount))
                .add("deleted", (root, query, cb) -> root.get(SupplyEntity_.deleted))
                .add("commited", (root, query, cb) -> root.get(SupplyEntity_.commited))
                .add("comment", (root, query, cb) -> root.get(SupplyEntity_.comment))
                .add("warehouse.id", (root, query, cb) -> root
                        .get(SupplyEntity_.warehouse).get(WarehouseEntity_.id))
                .add("warehouse.name", (root, query, cb) -> root
                        .get(SupplyEntity_.warehouse).get(WarehouseEntity_.name))
                .add("counterparty.id", (root, query, cb) -> root
                        .get(SupplyEntity_.counterparty).get(CounterpartyEntity_.id))
                .add("counterparty.name", (root, query, cb) -> root
                        .get(SupplyEntity_.counterparty).get(CounterpartyEntity_.name))
                .add("paidAmount", (root, query, cb) -> {
                    Subquery<BigDecimal> subQuery = query.subquery(BigDecimal.class);
                    Root<PaidDocumentEntity> PaidDocumentRoot = subQuery.from(PaidDocumentEntity.class);
                    subQuery.select(cb.sum(PaidDocumentRoot.get(PaidDocumentEntity_.amount)));
                    subQuery.where(cb.and(
                            cb.equal(PaidDocumentRoot.get(PaidDocumentEntity_.document), root),
                            cb.equal(PaidDocumentRoot.get(PaidDocumentEntity_.payment)
                                    .get(DocumentEntity_.counterparty), root.get(DocumentEntity_.counterparty))));
                    return subQuery;
                })
                .add("notPaidAmount", (root, query, cb) -> cb.sum(root.get(SupplyEntity_.amount), 1))
                .build();
        Search<SupplyListResponse> search = searchFactory.createCriteriaSearch(
                SupplyListResponse.class, List.of("name", "counterparty.name"),
                SupplyEntity.class, attributes);
        return search.getResult(queryParams);
    }

    @Transactional
    public UUID createApi(SupplyRequest request) {
        SupplyEntity supply = supplyMapper.toEntity(request);
        if (supply.getName() == null) {
            supply.setName(documentService.findNextName(SupplyEntity.class));
        }
        documentService.validateName(supply);
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
}
