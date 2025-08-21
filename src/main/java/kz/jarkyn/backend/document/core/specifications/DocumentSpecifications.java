package kz.jarkyn.backend.document.core.specifications;

import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity_;
import kz.jarkyn.backend.document.core.model.DocumentTypeEntity;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecifications {
    public static Specification<DocumentEntity> type(DocumentTypeEntity type) {
        return (root, query, cb) -> cb.equal(root.get(DocumentEntity_.type), type);
    }

    public static Specification<DocumentEntity> warehouse(WarehouseEntity warehouse) {
        return (root, query, cb) -> cb.equal(root.get(DocumentEntity_.warehouse), warehouse);
    }
}