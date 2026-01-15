package kz.jarkyn.backend.audit.specifications;

import kz.jarkyn.backend.audit.model.AuditEntity;
import kz.jarkyn.backend.audit.model.AuditEntity_;
import kz.jarkyn.backend.user.model.SessionEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.UUID;

public class AuditSpecifications {
    public static Specification<AuditEntity> entityId(UUID entityId) {
        return (root, query, cb) -> cb.equal(root.get(AuditEntity_.entityId), entityId);
    }

    public static Specification<AuditEntity> relatedEntityId(UUID relatedEntityId) {
        return (root, query, cb) -> cb.equal(root.get(AuditEntity_.relatedEntityId), relatedEntityId);
    }

    public static Specification<AuditEntity> fieldName(String fieldName) {
        return (root, query, cb) -> cb.equal(root.get(AuditEntity_.fieldName), fieldName);
    }

    public static Specification<AuditEntity> session(SessionEntity session) {
        return (root, query, cb) -> cb.equal(root.get(AuditEntity_.session), session);
    }

    public static Specification<AuditEntity> createdLessThanSecond(Instant instant) {
        return (root, query, cb) -> cb.greaterThan(
                root.get(AuditEntity_.createdAt),
                instant.minusSeconds(1)
        );
    }
}