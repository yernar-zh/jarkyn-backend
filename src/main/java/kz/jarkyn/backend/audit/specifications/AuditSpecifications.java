package kz.jarkyn.backend.audit.specifications;

import kz.jarkyn.backend.audit.model.AuditEntity;
import kz.jarkyn.backend.audit.model.AuditEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.operation.mode.TurnoverEntity;
import kz.jarkyn.backend.operation.mode.TurnoverEntity_;
import kz.jarkyn.backend.warehouse.model.GoodEntity;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.UUID;

public class AuditSpecifications {
    public static Specification<AuditEntity> entityId(UUID entityId) {
        return (root, query, cb) -> cb.equal(root.get(AuditEntity_.entityId), entityId);
    }

    public static Specification<AuditEntity> parentEntityId(UUID parentEntityId) {
        return (root, query, cb) -> cb.equal(root.get(AuditEntity_.entityParentId), parentEntityId);
    }

    public static Specification<AuditEntity> fieldName(String fieldName) {
        return (root, query, cb) -> cb.equal(root.get(AuditEntity_.fieldName), fieldName);
    }
}