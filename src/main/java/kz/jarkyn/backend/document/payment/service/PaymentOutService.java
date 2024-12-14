
package kz.jarkyn.backend.document.payment.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.payment.mapper.PaymentOutMapper;
import kz.jarkyn.backend.document.payment.model.PaymentOutEntity;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutDetailResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutRequest;
import kz.jarkyn.backend.document.payment.repository.PaymentOutRepository;
import kz.jarkyn.backend.good.model.dto.GoodResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentOutService {
    private final PaymentOutRepository paymentOutRepository;
    private final PaymentOutMapper paymentOutMapper;
    private final PaidDocumentService paidDocumentService;
    private final DocumentService documentService;
    private final AuditService auditService;

    public PaymentOutService(
            PaymentOutRepository paymentOutRepository,
            PaymentOutMapper paymentOutMapper,
            PaidDocumentService paidDocumentService,
            DocumentService documentService,
            AuditService auditService
    ) {
        this.paymentOutRepository = paymentOutRepository;
        this.paymentOutMapper = paymentOutMapper;
        this.paidDocumentService = paidDocumentService;
        this.documentService = documentService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public PaymentOutDetailResponse findApiById(UUID id) {
        PaymentOutEntity paymentOut = paymentOutRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<PaidDocumentResponse> paidDocuments = paidDocumentService.findResponseByPayment(paymentOut);
        return paymentOutMapper.toResponse(paymentOut, paidDocuments);
    }

    @Transactional(readOnly = true)
    public List<GoodResponse> findApiByFilter(QueryParams queryParams) {
        return null;
    }

    @Transactional
    public UUID createApi(PaymentOutRequest request) {
        PaymentOutEntity paymentOut = paymentOutMapper.toEntity(request);
        if (paymentOut.getName() == null) {
            paymentOut.setName(documentService.findNextName(PaymentOutEntity.class));
        }
        documentService.validateName(paymentOut);
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
}
