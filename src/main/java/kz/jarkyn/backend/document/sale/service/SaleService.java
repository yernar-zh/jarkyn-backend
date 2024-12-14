
package kz.jarkyn.backend.document.sale.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.core.service.ItemService;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.payment.service.PaidDocumentService;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.document.sale.model.dto.SaleDetailResponse;
import kz.jarkyn.backend.document.sale.model.dto.SaleRequest;
import kz.jarkyn.backend.document.sale.repository.SaleRepository;
import kz.jarkyn.backend.document.sale.mapper.SaleMapper;
import kz.jarkyn.backend.good.model.dto.GoodResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SaleService {
    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final ItemService itemService;
    private final PaidDocumentService paidDocumentService;
    private final DocumentService documentService;
    private final AuditService auditService;

    public SaleService(
            SaleRepository saleRepository,
            SaleMapper saleMapper,
            ItemService itemService,
            PaidDocumentService paidDocumentService,
            DocumentService documentService,
            AuditService auditService) {
        this.saleRepository = saleRepository;
        this.saleMapper = saleMapper;
        this.itemService = itemService;
        this.paidDocumentService = paidDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public SaleDetailResponse findApiById(UUID id) {
        SaleEntity sale = saleRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<ItemResponse> items = itemService.findApiByDocument(sale);
        List<PaidDocumentResponse> inPaidDocuments = paidDocumentService.findInResponseByDocument(sale);
        List<PaidDocumentResponse> outPaidDocuments = paidDocumentService.findOutResponseByDocument(sale);
        return saleMapper.toDetailResponse(sale, items, inPaidDocuments);
    }

    @Transactional(readOnly = true)
    public List<GoodResponse> findApiByFilter(QueryParams queryParams) {
        return null;
    }

    @Transactional
    public UUID createApi(SaleRequest request) {
        SaleEntity sale = saleMapper.toEntity(request);
        if (sale.getName() == null) {
            sale.setName(documentService.findNextName(SaleEntity.class));
        }
        documentService.validateName(sale);
        saleRepository.save(sale);
        auditService.saveChanges(sale);
        itemService.saveApi(sale, request.getItems());
        return sale.getId();
    }

    @Transactional
    public void editApi(UUID id, SaleRequest request) {
        SaleEntity sale = saleRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        documentService.validateName(sale);
        saleMapper.editEntity(sale, request);
        auditService.saveChanges(sale);
        itemService.saveApi(sale, request.getItems());
    }
}
