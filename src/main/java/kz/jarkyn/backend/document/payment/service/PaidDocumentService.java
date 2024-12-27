package kz.jarkyn.backend.document.payment.service;


import kz.jarkyn.backend.core.utils.EntityDivider;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.payment.mapper.PaidDocumentMapper;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity;
import kz.jarkyn.backend.document.payment.model.PaymentInEntity;
import kz.jarkyn.backend.document.payment.model.PaymentOutEntity;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentRequest;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.payment.repository.PaidDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaidDocumentService {
    private final PaidDocumentRepository paidDocumentRepository;
    private final PaidDocumentMapper paidDocumentMapper;

    public PaidDocumentService(
            PaidDocumentRepository paidDocumentRepository,
            PaidDocumentMapper paidDocumentMapper) {
        this.paidDocumentRepository = paidDocumentRepository;
        this.paidDocumentMapper = paidDocumentMapper;
    }

    @Transactional(readOnly = true)
    public List<PaidDocumentResponse> findResponseByPayment(DocumentEntity payment) {
        return paidDocumentMapper.toResponse(paidDocumentRepository.findByPayment(payment));
    }

    @Transactional(readOnly = true)
    public List<PaidDocumentResponse> findResponseByDocument(DocumentEntity document) {
        return paidDocumentMapper.toResponse(paidDocumentRepository.findByDocument(document));
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
                paidDocumentRepository.findByDocument(payment), paidDocumentRequests);
        for (EntityDivider<PaidDocumentEntity, PaidDocumentRequest>.Entry entry : divider.newReceived()) {
            PaidDocumentEntity paidDocument = paidDocumentMapper.toEntity(entry.getReceived());
            paidDocument.setDocument(payment);
            paidDocumentRepository.save(paidDocument);
        }
        for (EntityDivider<PaidDocumentEntity, PaidDocumentRequest>.Entry entry : divider.edited()) {
            paidDocumentMapper.editEntity(entry.getCurrent(), entry.getReceived());
        }
        paidDocumentRepository.deleteAll(divider.skippedCurrent());
    }
}