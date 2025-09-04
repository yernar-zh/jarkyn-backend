package kz.jarkyn.backend.user.spesification;

import kz.jarkyn.backend.user.model.*;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {
    public static Specification<UserEntity> role(RoleEntity role) {
        return (root, query, cb) -> cb.equal(root.get(UserEntity_.role), role);
    }

    public static Specification<UserEntity> phoneNumber(String phoneNumber) {
        return (root, query, cb) -> cb.equal(root.get(UserEntity_.phoneNumber), phoneNumber);
    }
}