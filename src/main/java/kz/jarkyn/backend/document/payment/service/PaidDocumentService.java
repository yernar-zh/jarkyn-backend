package kz.jarkyn.backend.document.payment.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.utils.EntityDivider;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.payment.mapper.PaidDocumentMapper;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity;
import kz.jarkyn.backend.document.payment.model.PaymentInEntity;
import kz.jarkyn.backend.document.payment.model.PaymentOutEntity;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentRequest;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.payment.repository.PaidDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaidDocumentService {
    private final PaidDocumentRepository paidDocumentRepository;
    private final PaidDocumentMapper paidDocumentMapper;
    private final AuditService auditService;

    public PaidDocumentService(
            PaidDocumentRepository paidDocumentRepository,
            PaidDocumentMapper paidDocumentMapper, DocumentService documentService, AuditService auditService) {
        this.paidDocumentRepository = paidDocumentRepository;
        this.paidDocumentMapper = paidDocumentMapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<PaidDocumentResponse> findResponseByPayment(DocumentEntity payment) {
        return paidDocumentRepository.findByPayment(payment).stream().map(entity -> {
            BigDecimal paidAmount = paidDocumentRepository.findByDocument(entity.getDocument()).stream()
                    .map(PaidDocumentEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            return paidDocumentMapper.toResponse(entity, entity.getDocument().getAmount().subtract(paidAmount));
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<PaidDocumentResponse> findResponseByDocument(DocumentEntity document) {
        return paidDocumentRepository.findByDocument(document).stream().map(entity -> {
            BigDecimal paidAmount = paidDocumentRepository.findByPayment(entity.getPayment()).stream()
                    .map(PaidDocumentEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            return paidDocumentMapper.toResponse(entity, entity.getPayment().getAmount().subtract(paidAmount));
        }).toList();
    }

    @Transactional
    public void saveApi(PaymentInEntity paymentIn, List<PaidDocumentRequest> paidDocumentRequests) {
        save(paymentIn, paidDocumentRequests);
    }

    @Transactional
    public void saveApi(PaymentOutEntity paymentOut, List<PaidDocumentRequest> paidDocumentRequests) {
        save(paymentOut, paidDocumentRequests);
    }

    private void save(DocumentEntity payment, List<PaidDocumentRequest> paidDocumentRequests) {
        EntityDivider<PaidDocumentEntity, PaidDocumentRequest> divider = new EntityDivider<>(
                paidDocumentRepository.findByPayment(payment), paidDocumentRequests);
        for (EntityDivider<PaidDocumentEntity, PaidDocumentRequest>.Entry entry : divider.newReceived()) {
            PaidDocumentEntity paidDocument = paidDocumentMapper.toEntity(entry.getReceived());
            paidDocument.setPayment(payment);
            paidDocumentRepository.save(paidDocument);
            auditService.saveEntity(paidDocument, paidDocument.getPayment(), "paidDocuments");
        }
        for (EntityDivider<PaidDocumentEntity, PaidDocumentRequest>.Entry entry : divider.edited()) {
            paidDocumentMapper.editEntity(entry.getCurrent(), entry.getReceived());
            auditService.saveEntity(entry.getCurrent(), entry.getCurrent().getPayment(), "paidDocuments");
        }
        for (PaidDocumentEntity paidDocument : divider.skippedCurrent()) {
            paidDocumentRepository.delete(paidDocument);
            auditService.delete(paidDocument, paidDocument.getPayment());
        }
    }
}
