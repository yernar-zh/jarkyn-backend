
package kz.jarkyn.backend.document.core.service;


import kz.jarkyn.backend.document.core.model.DocumentTypeEntity;
import kz.jarkyn.backend.document.core.repository.DocumentTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentTypeService {
    private final DocumentTypeRepository documentTypeRepository;

    public DocumentTypeService(DocumentTypeRepository documentTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
    }

    @Transactional(readOnly = true)
    public DocumentTypeEntity findPaymentOut() {
        return findByCode(DocumentTypeCode.PAYMENT_OUT);
    }

    @Transactional(readOnly = true)
    public boolean isPaymentOut(DocumentTypeEntity documentType) {
        return documentType.getCode().equals(DocumentTypeCode.PAYMENT_OUT.name());
    }

    @Transactional(readOnly = true)
    public boolean isPaymentIn(DocumentTypeEntity documentType) {
        return documentType.getCode().equals(DocumentTypeCode.PAYMENT_IN.name());
    }

    @Transactional(readOnly = true)
    public boolean isPayment(DocumentTypeEntity documentType) {
        return isPaymentIn(documentType) || isPaymentOut(documentType);
    }

    @Transactional(readOnly = true)
    public DocumentTypeEntity findByCode(DocumentTypeCode code) {
        return documentTypeRepository.findByCode(code.name()).orElseThrow();
    }

    public enum DocumentTypeCode {
        SALE, SUPPLY, PAYMENT_IN, PAYMENT_OUT
    }
}
