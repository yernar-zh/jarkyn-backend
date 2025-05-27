package kz.jarkyn.backend.document.payment.service;


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
import kz.jarkyn.backend.document.payment.model.*;
import kz.jarkyn.backend.party.model.AccountEntity;
import kz.jarkyn.backend.party.model.AccountEntity_;
import kz.jarkyn.backend.party.model.PartyEntity_;
import kz.jarkyn.backend.party.model.OrganizationEntity_;
import kz.jarkyn.backend.party.service.AccountService;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.payment.mapper.PaymentOutMapper;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutListResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutRequest;
import kz.jarkyn.backend.document.payment.repository.PaymentOutRepository;
import kz.jarkyn.backend.operation.service.CashFlowService;
import kz.jarkyn.backend.global.model.CurrencyEntity_;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentOutService {
    private final PaymentOutRepository paymentOutRepository;
    private final PaymentOutMapper paymentOutMapper;
    private final PaidDocumentService paidDocumentService;
    private final DocumentService documentService;
    private final AuditService auditService;
    private final SearchFactory searchFactory;
    private final AccountService accountService;
    private final CashFlowService cashFlowService;

    public PaymentOutService(
            PaymentOutRepository paymentOutRepository,
            PaymentOutMapper paymentOutMapper,
            PaidDocumentService paidDocumentService,
            DocumentService documentService,
            AuditService auditService,
            SearchFactory searchFactory,
            AccountService accountService,
            CashFlowService cashFlowService
    ) {
        this.paymentOutRepository = paymentOutRepository;
        this.paymentOutMapper = paymentOutMapper;
        this.paidDocumentService = paidDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
        this.accountService = accountService;
        this.cashFlowService = cashFlowService;
    }

    @Transactional(readOnly = true)
    public PaymentOutResponse findApiById(UUID id) {
        PaymentOutEntity paymentOut = paymentOutRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<PaidDocumentResponse> paidDocuments = paidDocumentService.findResponseByPayment(paymentOut);
        return paymentOutMapper.toResponse(paymentOut, paidDocuments);
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentOutListResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<PaymentOutEntity> attributes = CriteriaAttributes.<PaymentOutEntity>builder()
                .add("id", (root) -> root.get(PaymentOutEntity_.id))
                .add("name", (root) -> root.get(PaymentOutEntity_.name))
                .add("organization.id", (root) -> root.get(PaymentOutEntity_.organization).get(OrganizationEntity_.id))
                .add("organization.name", (root) -> root.get(PaymentOutEntity_.organization).get(OrganizationEntity_.name))
                .add("account.id", (root) -> root.get(PaymentOutEntity_.account).get(AccountEntity_.id))
                .add("account.name", (root) -> root.get(PaymentOutEntity_.account).get(AccountEntity_.name))
                .add("counterparty.id", (root) -> root.get(PaymentOutEntity_.counterparty).get(PartyEntity_.id))
                .add("counterparty.name", (root) -> root.get(PaymentOutEntity_.counterparty).get(PartyEntity_.name))
                .add("moment", (root) -> root.get(PaymentOutEntity_.moment))
                .add("currency.id", (root) -> root.get(PaymentOutEntity_.currency).get(CurrencyEntity_.id))
                .add("currency.name", (root) -> root.get(PaymentOutEntity_.currency).get(CurrencyEntity_.name))
                .add("exchangeRate", (root) -> root.get(PaymentOutEntity_.exchangeRate))
                .add("amount", (root) -> root.get(PaymentOutEntity_.amount))
                .add("deleted", (root) -> root.get(PaymentOutEntity_.deleted))
                .add("commited", (root) -> root.get(PaymentOutEntity_.commited))
                .add("comment", (root) -> root.get(PaymentOutEntity_.comment))
                .add("receiptNumber", (root) -> root.get(PaymentOutEntity_.receiptNumber))
                .add("itemOfExpenditure", (root) -> root.get(PaymentOutEntity_.itemOfExpenditure))
                .add("purpose", (root) -> root.get(PaymentOutEntity_.purpose))
                .add("attachedAmount", (root, query, cb, map) -> {
                    Subquery<BigDecimal> subQuery = query.subquery(BigDecimal.class);
                    Root<PaidDocumentEntity> paidDocumentRoot = subQuery.from(PaidDocumentEntity.class);
                    subQuery.select(cb.sum(paidDocumentRoot.get(PaidDocumentEntity_.amount)));
                    subQuery.where(cb.equal(paidDocumentRoot.get(PaidDocumentEntity_.payment), root));
                    return subQuery;
                })
                .add("notAttachedAmount", (root, query, cb, map) -> cb.diff(
                        (Expression<Number>) map.get("amount"), (Expression<Number>) map.get("attachedAmount")))
                .build();
        Search<PaymentOutListResponse> search = searchFactory.createCriteriaSearch(
                PaymentOutListResponse.class, List.of("name", "counterparty.name"),
                PaymentOutEntity.class, attributes);
        return search.getResult(queryParams);
    }

    @Transactional
    public UUID createApi(PaymentOutRequest request) {
        PaymentOutEntity paymentOut = paymentOutMapper.toEntity(request);
        if (paymentOut.getName() == null) {
            paymentOut.setName(documentService.findNextName(PaymentOutEntity.class));
        }
        documentService.validateName(paymentOut);
        paymentOut.setDeleted(false);
        paymentOut.setCommited(false);
        paymentOutRepository.save(paymentOut);
        auditService.saveChanges(paymentOut);
        paidDocumentService.saveApi(paymentOut, request.getPaidDocuments());
        return paymentOut.getId();
    }

    @Transactional
    public void editApi(UUID id, PaymentOutRequest request) {
        PaymentOutEntity paymentOut = paymentOutRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        documentService.validateName(paymentOut);
        paymentOutMapper.editEntity(paymentOut, request);
        auditService.saveChanges(paymentOut);
        paidDocumentService.saveApi(paymentOut, request.getPaidDocuments());
    }

    @Transactional
    public void commit(UUID id) {
        PaymentOutEntity paymentOut = paymentOutRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        paymentOut.setCommited(Boolean.TRUE);
        auditService.saveChanges(paymentOut);
        AccountEntity account = accountService.findOrCreateForCounterparty(
                paymentOut.getOrganization(), paymentOut.getCounterparty(), paymentOut.getCurrency());
        cashFlowService.create(paymentOut, account, paymentOut.getAmount().negate());
        cashFlowService.create(paymentOut, paymentOut.getAccount(), paymentOut.getAmount().negate());
    }

    @Transactional
    public void undoCommit(UUID id) {
        PaymentOutEntity paymentOut = paymentOutRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        paymentOut.setCommited(Boolean.FALSE);
        auditService.saveChanges(paymentOut);
        cashFlowService.delete(paymentOut);
    }
}
