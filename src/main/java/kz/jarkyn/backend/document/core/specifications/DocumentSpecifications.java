package kz.jarkyn.backend.document.core.specifications;

import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity_;
import kz.jarkyn.backend.document.core.model.DocumentTypeEntity;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecifications {
    public static Specification<DocumentEntity> type(DocumentTypeEntity type) {
        return (root, query, cb) -> cb.equal(root.get(DocumentEntity_.type), type);
    }
}