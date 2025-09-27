package kz.jarkyn.backend.document.bind.specifications;

import kz.jarkyn.backend.document.bind.model.BindDocumentEntity;
import kz.jarkyn.backend.document.bind.model.BindDocumentEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import org.springframework.data.jpa.domain.Specification;

public class BindDocumentSpecifications {
    public static Specification<BindDocumentEntity> primaryDocument(DocumentEntity primaryDocument) {
        return (root, query, cb) -> cb.equal(root.get(BindDocumentEntity_.primaryDocument), primaryDocument);
    }
    public static Specification<BindDocumentEntity> relatedDocument(DocumentEntity relatedDocument) {
        return (root, query, cb) -> cb.equal(root.get(BindDocumentEntity_.relatedDocument), relatedDocument);
    }
}