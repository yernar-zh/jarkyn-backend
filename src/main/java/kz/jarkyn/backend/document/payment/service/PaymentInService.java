
package kz.jarkyn.backend.document.payment.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.payment.mapper.PaymentInMapper;
import kz.jarkyn.backend.document.payment.model.PaymentInEntity;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInRequest;
import kz.jarkyn.backend.document.payment.repository.PaymentInRepository;
import kz.jarkyn.backend.good.model.dto.GoodResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentInService {
    private final PaymentInRepository paymentInRepository;
    private final PaymentInMapper paymentInMapper;
    private final PaidDocumentService paidDocumentService;
    private final DocumentService documentService;
    private final AuditService auditService;

    public PaymentInService(
            PaymentInRepository paymentInRepository,
            PaymentInMapper paymentInMapper,
            PaidDocumentService paidDocumentService,
            DocumentService documentService,
            AuditService auditService
    ) {
        this.paymentInRepository = paymentInRepository;
        this.paymentInMapper = paymentInMapper;
        this.paidDocumentService = paidDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public PaymentInResponse findApiById(UUID id) {
        PaymentInEntity paymentIn = paymentInRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<PaidDocumentResponse> paidDocuments = paidDocumentService.findResponseByPayment(paymentIn);
        return paymentInMapper.toResponse(paymentIn, paidDocuments);
    }

    @Transactional(readOnly = true)
    public List<GoodResponse> findApiByFilter(QueryParams queryParams) {
        return null;
    }

    @Transactional
    public UUID createApi(PaymentInRequest request) {
        PaymentInEntity paymentIn = paymentInMapper.toEntity(request);
        if (paymentIn.getName() == null) {
            paymentIn.setName(documentService.findNextName(PaymentInEntity.class));
        }
        documentService.validateName(paymentIn);
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
}
