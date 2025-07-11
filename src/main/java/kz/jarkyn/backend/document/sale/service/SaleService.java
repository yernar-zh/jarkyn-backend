
package kz.jarkyn.backend.document.sale.service;


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
import kz.jarkyn.backend.party.model.AccountEntity;
import kz.jarkyn.backend.party.model.PartyEntity_;
import kz.jarkyn.backend.party.model.OrganizationEntity_;
import kz.jarkyn.backend.party.service.AccountService;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.core.service.ItemService;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity_;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.payment.service.PaidDocumentService;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.document.sale.model.SaleState;
import kz.jarkyn.backend.document.sale.model.dto.SaleListResponse;
import kz.jarkyn.backend.document.sale.model.dto.SaleRequest;
import kz.jarkyn.backend.document.sale.model.dto.SaleResponse;
import kz.jarkyn.backend.document.sale.repository.SaleRepository;
import kz.jarkyn.backend.document.sale.mapper.SaleMapper;
import kz.jarkyn.backend.document.sale.model.SaleEntity_;
import kz.jarkyn.backend.operation.mode.TurnoverEntity;
import kz.jarkyn.backend.operation.mode.TurnoverEntity_;
import kz.jarkyn.backend.operation.service.CashFlowService;
import kz.jarkyn.backend.global.model.CurrencyEntity_;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity_;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final SearchFactory searchFactory;
    private final CashFlowService cashFlowService;
    private final AccountService accountService;

    public SaleService(
            SaleRepository saleRepository,
            SaleMapper saleMapper,
            ItemService itemService,
            PaidDocumentService paidDocumentService,
            DocumentService documentService,
            AuditService auditService,
            SearchFactory searchFactory,
            CashFlowService cashFlowService,
            AccountService accountService) {
        this.saleRepository = saleRepository;
        this.saleMapper = saleMapper;
        this.itemService = itemService;
        this.paidDocumentService = paidDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
        this.cashFlowService = cashFlowService;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public SaleResponse findApiById(UUID id) {
        SaleEntity sale = saleRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<ItemResponse> items = itemService.findApiByDocument(sale);
        List<PaidDocumentResponse> paidDocuments = paidDocumentService.findResponseByDocument(sale);
        return saleMapper.toResponse(sale, items, paidDocuments);
    }

    @Transactional(readOnly = true)
    public PageResponse<SaleListResponse> findApiByFilter(QueryParams queryParams) {
        return null;
    }

    @Transactional
    public UUID createApi(SaleRequest request) {
        SaleEntity sale = saleMapper.toEntity(request);
        if (sale.getName() == null) {
            sale.setName(documentService.findNextName(SaleEntity.class));
        }
        documentService.validateName(sale);
        sale.setDeleted(false);
        sale.setCommited(false);
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

    @Transactional
    public void commit(UUID id) {
        SaleEntity sale = saleRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        sale.setState(SaleState.SHIPPED);
        sale.setCommited(Boolean.TRUE);
        auditService.saveChanges(sale);
        itemService.createNegativeTurnover(sale);
        AccountEntity account = accountService.findOrCreateForCounterparty(
                sale.getOrganization(), sale.getCounterparty(), sale.getCurrency());
        cashFlowService.create(sale, account, sale.getAmount().negate());
    }

    @Transactional
    public void undoCommit(UUID id) {
        SaleEntity sale = saleRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        sale.setCommited(Boolean.FALSE);
        auditService.saveChanges(sale);
        itemService.deleteTurnover(sale);
        cashFlowService.delete(sale);
    }
}
