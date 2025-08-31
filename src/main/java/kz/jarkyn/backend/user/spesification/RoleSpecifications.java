package kz.jarkyn.backend.user.spesification;

import kz.jarkyn.backend.user.model.RoleEntity;
import kz.jarkyn.backend.user.model.RoleEntity_;
import org.springframework.data.jpa.domain.Specification;

public class RoleSpecifications {
    public static Specification<RoleEntity> system() {
        return (root, query, cb) -> cb.equal(root.get(RoleEntity_.code), "SYSTEM");
    }

    public static Specification<RoleEntity> owner() {
        return (root, query, cb) -> cb.equal(root.get(RoleEntity_.code), "OWNER");
    }
}