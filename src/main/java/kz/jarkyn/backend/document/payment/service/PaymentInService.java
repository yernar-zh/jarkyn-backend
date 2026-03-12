package kz.jarkyn.backend.document.payment.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.config.AppRabbitTemplate;
import kz.jarkyn.backend.core.config.RabbitRoutingKeys;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.bind.service.BindDocumentService;
import kz.jarkyn.backend.document.core.service.DocumentSearchService;
import kz.jarkyn.backend.document.core.service.DocumentTypeService;
import kz.jarkyn.backend.document.payment.model.*;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentRequest;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentResponse;
import kz.jarkyn.backend.party.model.*;
import kz.jarkyn.backend.party.service.AccountService;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.payment.mapper.PaymentInMapper;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInListResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInRequest;
import kz.jarkyn.backend.document.payment.repository.PaymentInRepository;
import kz.jarkyn.backend.operation.service.CashFlowService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PaymentInService {
    private final PaymentInRepository paymentInRepository;
    private final PaymentInMapper paymentInMapper;
    private final BindDocumentService bindDocumentService;
    private final DocumentService documentService;
    private final AuditService auditService;
    private final AccountService accountService;
    private final CashFlowService cashFlowService;
    private final DocumentTypeService documentTypeService;
    private final DocumentSearchService documentSearchService;
    private final AppRabbitTemplate appRabbitTemplate;

    public PaymentInService(
            PaymentInRepository paymentInRepository,
            PaymentInMapper paymentInMapper,
            BindDocumentService bindDocumentService,
            DocumentService documentService,
            AuditService auditService,
            AccountService accountService,
            CashFlowService cashFlowService,
            DocumentTypeService documentTypeService,
            DocumentSearchService documentSearchService,
            AppRabbitTemplate appRabbitTemplate) {
        this.paymentInRepository = paymentInRepository;
        this.paymentInMapper = paymentInMapper;
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
    public PaymentInResponse findApiById(UUID id) {
        PaymentInEntity paymentIn = paymentInRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<BindDocumentResponse> bindDocuments = bindDocumentService.findResponseByPrimaryDocument(paymentIn);
        return paymentInMapper.toResponse(paymentIn, bindDocuments);
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentInListResponse> findApiByFilter(QueryParams queryParams) {
        return documentSearchService.findApiByFilter(PaymentInListResponse.class, queryParams, documentTypeService.findPaymentIn());
    }

    @Transactional
    public UUID createApi(PaymentInRequest request) {
        PaymentInEntity paymentIn = paymentInMapper.toEntity(request);
        paymentIn.setType(documentTypeService.findPaymentIn());
        if (Strings.isBlank(paymentIn.getName())) {
            paymentIn.setName(documentService.findNextName(paymentIn.getType()));
        } else {
            documentService.validateName(paymentIn);
        }
        paymentIn.setDeleted(false);
        paymentInRepository.save(paymentIn);
        save(paymentIn, request.getBindDocuments());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, paymentIn.getId());
        return paymentIn.getId();
    }

    @Transactional
    public void editApi(UUID id, PaymentInRequest request) {
        PaymentInEntity paymentIn = paymentInRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        documentService.validateName(paymentIn);
        paymentInMapper.editEntity(paymentIn, request);
        save(paymentIn, request.getBindDocuments());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, paymentIn.getId());
    }

    private void save(PaymentInEntity paymentIn, List<BindDocumentRequest> bindDocumentRequests) {
        auditService.saveEntity(paymentIn);
        bindDocumentService.save(paymentIn, bindDocumentRequests);

        AccountEntity account = accountService.findOrCreateForCounterparty(
                paymentIn.getOrganization(), paymentIn.getCounterparty(), paymentIn.getCurrency());
        if (Boolean.TRUE.equals(paymentIn.getCommited())) {
            cashFlowService.change(paymentIn, account, paymentIn.getAmount());
            cashFlowService.change(paymentIn, paymentIn.getAccount(), paymentIn.getAmount());

            Set<AccountEntity> accountsToKeep = new HashSet<>();
            accountsToKeep.add(account);
            if (paymentIn.getAccount() != null) {
                accountsToKeep.add(paymentIn.getAccount());
            }
            cashFlowService.deleteAll(paymentIn, accountsToKeep);
        } else {
            cashFlowService.deleteAll(paymentIn, Set.of());
        }
    }

    @Transactional
    public void delete(UUID id) {
        PaymentInEntity paymentIn = paymentInRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (paymentIn.getDeleted()) return;
        paymentIn.setCommited(Boolean.FALSE);
        paymentIn.setDeleted(Boolean.TRUE);
        auditService.delete(paymentIn);
        cashFlowService.deleteAll(paymentIn, Set.of());
        bindDocumentService.save(paymentIn, List.of());
        appRabbitTemplate.sendAfterCommit(RabbitRoutingKeys.DOCUMENT_SEARCH, paymentIn.getId());
    }
}
