package kz.jarkyn.backend.document.payment.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.config.AppRabbitTemplate;
import kz.jarkyn.backend.core.config.RabbitRoutingKeys;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.document.bind.service.BindDocumentService;
import kz.jarkyn.backend.document.core.service.DocumentSearchService;
import kz.jarkyn.backend.document.core.service.DocumentTypeService;
import kz.jarkyn.backend.document.payment.model.*;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentResponse;
import kz.jarkyn.backend.party.model.*;
import kz.jarkyn.backend.party.service.AccountService;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.payment.mapper.PaymentOutMapper;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutListResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutRequest;
import kz.jarkyn.backend.document.payment.repository.PaymentOutRepository;
import kz.jarkyn.backend.operation.service.CashFlowService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentOutService {
    private final PaymentOutRepository paymentOutRepository;
    private final PaymentOutMapper paymentOutMapper;
    private final BindDocumentService bindDocumentService;
    private final DocumentService documentService;
    private final AuditService auditService;
    private final AccountService accountService;
    private final CashFlowService cashFlowService;
    private final DocumentTypeService documentTypeService;
    private final DocumentSearchService documentSearchService;
    private final AppRabbitTemplate appRabbitTemplate;

    public PaymentOutService(
            PaymentOutRepository paymentOutRepository,
            PaymentOutMapper paymentOutMapper,
            BindDocumentService bindDocumentService,
            DocumentService documentService,
            AuditService auditService,
            AccountService accountService,
            CashFlowService cashFlowService,
            DocumentTypeService documentTypeService,
            DocumentSearchService documentSearchService, AppRabbitTemplate appRabbitTemplate) {
        this.paymentOutRepository = paymentOutRepository;
        this.paymentOutMapper = paymentOutMapper;
        this.bindDocumentService = bindDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
        this.accountService = accountService;
        this.cashFlowService = cashFlowService;
        this.documentTypeService = documentTypeService;
        this.documentSearchService = documentSearchService;
        this.appRabbitTemplate = appRabbitTemplate;
    }

    @Transactional(readOnly = true)
    public PaymentOutResponse findApiById(UUID id) {
        PaymentOutEntity paymentOut = paymentOutRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<BindDocumentResponse> bindDocuments = bindDocumentService.findResponseByPrimaryDocument(paymentOut);
        return paymentOutMapper.toResponse(paymentOut, bindDocuments);
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentOutListResponse> findApiByFilter(QueryParams queryParams) {
        return documentSearchService.findApiByFilter(PaymentOutListResponse.class, queryParams, documentTypeService.findPaymentOut());
    }

    @Transactional
    public UUID createApi(PaymentOutRequest request) {
        PaymentOutEntity paymentOut = paymentOutMapper.toEntity(request);
        paymentOut.setType(documentTypeService.findPaymentOut());
        if (Strings.isBlank(paymentOut.getName())) {
            paymentOut.setName(documentService.findNextName(paymentOut.getType()));
        } else {
            documentService.validateName(paymentOut);
        }
        paymentOut.setDeleted(false);
        paymentOut.setCommited(false);
        paymentOutRepository.save(paymentOut);
        auditService.saveEntity(paymentOut);
        bindDocumentService.save(paymentOut, request.getBindDocuments());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, paymentOut.getId());
        return paymentOut.getId();
    }

    @Transactional
    public void editApi(UUID id, PaymentOutRequest request) {
        PaymentOutEntity paymentOut = paymentOutRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        documentService.validateName(paymentOut);
        paymentOutMapper.editEntity(paymentOut, request);
        auditService.saveEntity(paymentOut);
        bindDocumentService.save(paymentOut, request.getBindDocuments());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, paymentOut.getId());
    }

    @Transactional
    public void commit(UUID id) {
        PaymentOutEntity paymentOut = paymentOutRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        paymentOut.setCommited(Boolean.TRUE);
        auditService.commit(paymentOut);
        AccountEntity account = accountService.findOrCreateForCounterparty(
                paymentOut.getOrganization(), paymentOut.getCounterparty(), paymentOut.getCurrency());
        cashFlowService.create(paymentOut, account, paymentOut.getAmount().negate());
        cashFlowService.create(paymentOut, paymentOut.getAccount(), paymentOut.getAmount().negate());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, paymentOut.getId());
    }

    @Transactional
    public void undoCommit(UUID id) {
        PaymentOutEntity paymentOut = paymentOutRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        paymentOut.setCommited(Boolean.FALSE);
        auditService.undoCommit(paymentOut);
        cashFlowService.delete(paymentOut);
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, paymentOut.getId());
    }

    @Transactional
    public void delete(UUID id) {
        PaymentOutEntity paymentOut = paymentOutRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (paymentOut.getCommited()) ExceptionUtils.throwCommitedDeleteException();
        paymentOut.setDeleted(Boolean.TRUE);
        auditService.delete(paymentOut);
        bindDocumentService.save(paymentOut, List.of());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, paymentOut.getId());
    }
}
