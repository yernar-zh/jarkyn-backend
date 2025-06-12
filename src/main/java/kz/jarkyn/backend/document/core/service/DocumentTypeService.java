
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
    public DocumentTypeEntity findByCode(DocumentTypeCode code) {
        return documentTypeRepository.findByCode(code.name()).orElseThrow();
    }

    public enum DocumentTypeCode {
        SALE, SUPPLY, PAYMENT_IN, PAYMENT_OUT
    }
}
