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
import kz.jarkyn.backend.party.model.AccountEntity;
import kz.jarkyn.backend.party.model.AccountEntity_;
import kz.jarkyn.backend.party.model.PartyEntity_;
import kz.jarkyn.backend.party.model.OrganizationEntity_;
import kz.jarkyn.backend.party.service.AccountService;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.payment.mapper.PaymentInMapper;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity_;
import kz.jarkyn.backend.document.payment.model.PaymentInEntity;
import kz.jarkyn.backend.document.payment.model.PaymentInEntity_;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInListResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInRequest;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInResponse;
import kz.jarkyn.backend.document.payment.repository.PaymentInRepository;
import kz.jarkyn.backend.operation.service.CashFlowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentInService {
    private final PaymentInRepository paymentInRepository;
    private final PaymentInMapper paymentInMapper;
    private final PaidDocumentService paidDocumentService;
    private final DocumentService documentService;
    private final AuditService auditService;
    private final SearchFactory searchFactory;
    private final AccountService accountService;
    private final CashFlowService cashFlowService;

    public PaymentInService(
            PaymentInRepository paymentInRepository,
            PaymentInMapper paymentInMapper,
            PaidDocumentService paidDocumentService,
            DocumentService documentService,
            AuditService auditService,
            SearchFactory searchFactory,
            AccountService accountService,
            CashFlowService cashFlowService
    ) {
        this.paymentInRepository = paymentInRepository;
        this.paymentInMapper = paymentInMapper;
        this.paidDocumentService = paidDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
        this.accountService = accountService;
        this.cashFlowService = cashFlowService;
    }

    @Transactional(readOnly = true)
    public PaymentInResponse findApiById(UUID id) {
        PaymentInEntity paymentIn = paymentInRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<PaidDocumentResponse> paidDocuments = paidDocumentService.findResponseByPayment(paymentIn);
        return paymentInMapper.toResponse(paymentIn, paidDocuments);
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentInListResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<PaymentInEntity> attributes = CriteriaAttributes.<PaymentInEntity>builder()
                .add("id", (root) -> root.get(PaymentInEntity_.id))
                .add("name", (root) -> root.get(PaymentInEntity_.name))
                .add("organization.id", (root) -> root
                        .get(PaymentInEntity_.organization).get(OrganizationEntity_.id))
                .add("organization.name", (root) -> root
                        .get(PaymentInEntity_.organization).get(OrganizationEntity_.name))
                .add("account.id", (root) -> root
                        .get(PaymentInEntity_.account).get(AccountEntity_.id))
                .add("account.name", (root) -> root
                        .get(PaymentInEntity_.account).get(AccountEntity_.name))
                .add("counterparty.id", (root) -> root
                        .get(PaymentInEntity_.counterparty).get(PartyEntity_.id))
                .add("counterparty.name", (root) -> root
                        .get(PaymentInEntity_.counterparty).get(PartyEntity_.name))
                .add("moment", (root) -> root.get(PaymentInEntity_.moment))
                .add("currency", (root) -> root.get(PaymentInEntity_.currency))
                .add("exchangeRate", (root) -> root.get(PaymentInEntity_.exchangeRate))
                .add("amount", (root) -> root.get(PaymentInEntity_.amount))
                .add("deleted", (root) -> root.get(PaymentInEntity_.deleted))
                .add("commited", (root) -> root.get(PaymentInEntity_.commited))
                .add("comment", (root) -> root.get(PaymentInEntity_.comment))
                .add("receiptNumber", (root) -> root.get(PaymentInEntity_.receiptNumber))
                .add("attached", (root, query, cb, map) -> {
                    Subquery<BigDecimal> subQuery = query.subquery(BigDecimal.class);
                    Root<PaidDocumentEntity> paidDocumentRoot = subQuery.from(PaidDocumentEntity.class);
                    subQuery.select(cb.sum(paidDocumentRoot.get(PaidDocumentEntity_.amount)));
                    subQuery.where(cb.equal(paidDocumentRoot.get(PaidDocumentEntity_.payment), root));
                    return subQuery;
                })
                .add("notAttached", (root, query, cb, map) -> cb.diff(
                        (Expression<Number>) map.get("amount"), (Expression<Number>) map.get("attached")))
                .build();
        Search<PaymentInListResponse> search = searchFactory.createCriteriaSearch(
                PaymentInListResponse.class, List.of("name", "counterparty.name"),
                PaymentInEntity.class, attributes);
        return search.getResult(queryParams);
    }

    @Transactional
    public UUID createApi(PaymentInRequest request) {
        PaymentInEntity paymentIn = paymentInMapper.toEntity(request);
        if (paymentIn.getName() == null) {
            paymentIn.setName(documentService.findNextName(PaymentInEntity.class));
        }
        documentService.validateName(paymentIn);
        paymentIn.setDeleted(false);
        paymentIn.setCommited(false);
        paymentInRepository.save(paymentIn);
        auditService.saveChanges(paymentIn);
        paidDocumentService.saveApi(paymentIn, request.getPaidDocuments());
        return paymentIn.getId();
    }

    @Transactional
    public void editApi(UUID id, PaymentInRequest request) {
        PaymentInEntity paymentIn = paymentInRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        documentService.validateName(paymentIn);
        paymentInMapper.editEntity(paymentIn, request);
        auditService.saveChanges(paymentIn);
        paidDocumentService.saveApi(paymentIn, request.getPaidDocuments());
    }

    @Transactional
    public void commit(UUID id) {
        PaymentInEntity paymentIn = paymentInRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        paymentIn.setCommited(Boolean.TRUE);
        auditService.saveChanges(paymentIn);
        AccountEntity account = accountService.findOrCreateForCounterparty(
                paymentIn.getOrganization(), paymentIn.getCounterparty(), paymentIn.getCurrency());
        cashFlowService.create(paymentIn, account, paymentIn.getAmount());
        cashFlowService.create(paymentIn, paymentIn.getAccount(), paymentIn.getAmount());
    }

    @Transactional
    public void undoCommit(UUID id) {
        PaymentInEntity paymentIn = paymentInRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        paymentIn.setCommited(Boolean.FALSE);
        auditService.saveChanges(paymentIn);
        cashFlowService.delete(paymentIn);
    }
}
