package kz.jarkyn.backend.audit.specifications;

import kz.jarkyn.backend.audit.model.AuditEntity;
import kz.jarkyn.backend.audit.model.AuditEntity_;
import kz.jarkyn.backend.user.model.UserEntity;
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

    public static Specification<AuditEntity> user(UserEntity user) {
        return (root, query, cb) -> cb.equal(root.get(AuditEntity_.user), user);
    }

    public static Specification<AuditEntity> createdLessThanOneSecond() {
        return (root, query, cb) -> cb.greaterThan(
                root.get(AuditEntity_.createdAt),
                Instant.now().minusSeconds(1)
        );
    }
}