
package kz.jarkyn.backend.document.supply.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.core.service.ItemService;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.payment.service.PaidDocumentService;
import kz.jarkyn.backend.document.supply.model.SupplyEntity;
import kz.jarkyn.backend.document.supply.model.dto.SupplyListResponse;
import kz.jarkyn.backend.document.supply.model.dto.SupplyResponse;
import kz.jarkyn.backend.document.supply.model.dto.SupplyRequest;
import kz.jarkyn.backend.document.supply.repository.SupplyRepository;
import kz.jarkyn.backend.document.supply.mapper.SupplyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public SupplyService(
            SupplyRepository supplyRepository,
            SupplyMapper supplyMapper,
            ItemService itemService,
            PaidDocumentService paidDocumentService,
            DocumentService documentService,
            AuditService auditService
    ) {
        this.supplyRepository = supplyRepository;
        this.supplyMapper = supplyMapper;
        this.itemService = itemService;
        this.paidDocumentService = paidDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
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
//        Search<SupplierListResponse> search = searchFactory.createListSearch(
//                SupplierListResponse.class, List.of("name"),
//                () -> supplierRepository.findAll().stream().map(supplier -> {
//                    Pair<BigDecimal, Currency> account = accountService.findByCounterparty(supplier)
//                            .stream().findFirst()
//                            .map(o -> Pair.of(o.getBalance(), o.getCurrency()))
//                            .orElse(Pair.of(BigDecimal.ZERO, Currency.KZT));
//                    Tuple results = supplierRepository.findSupplyInfo(supplier);
//                    return supplierMapper.toResponse(supplier, account.getFirst(), account.getSecond(),
//                            results.get("firstSupplyMoment", LocalDateTime.class),
//                            results.get("lastSupplyMoment", LocalDateTime.class),
//                            results.get("totalSupplyCount", Long.class).intValue(),
//                            results.get("totalSupplyAmount", BigDecimal.class)
//                    );
//                }).toList());
//        return search.getResult(queryParams);
        return null;
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
