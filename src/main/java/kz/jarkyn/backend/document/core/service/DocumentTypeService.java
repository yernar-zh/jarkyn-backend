
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
    public DocumentTypeEntity findPaymentIn() {
        return findByCode(DocumentTypeCode.PAYMENT_IN);
    }

    @Transactional(readOnly = true)
    public DocumentTypeEntity findExpense() {
        return findByCode(DocumentTypeCode.EXPENSE);
    }

    @Transactional(readOnly = true)
    public DocumentTypeEntity findSupply() {
        return findByCode(DocumentTypeCode.SUPPLY);
    }

    @Transactional(readOnly = true)
    public DocumentTypeEntity findSale() {
        return findByCode(DocumentTypeCode.SALE);
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
    public boolean isSupply(DocumentTypeEntity documentType) {
        return documentType.getCode().equals(DocumentTypeCode.SUPPLY.name());
    }

    @Transactional(readOnly = true)
    public boolean isSale(DocumentTypeEntity documentType) {
        return documentType.getCode().equals(DocumentTypeCode.SALE.name());
    }

    @Transactional(readOnly = true)
    public boolean isExpense(DocumentTypeEntity documentType) {
        return documentType.getCode().equals(DocumentTypeCode.EXPENSE.name());
    }

    private DocumentTypeEntity findByCode(DocumentTypeCode code) {
        return documentTypeRepository.findByCode(code.name()).orElseThrow();
    }

    private enum DocumentTypeCode {
        SALE, SUPPLY, PAYMENT_IN, PAYMENT_OUT, EXPENSE;
    }
}
