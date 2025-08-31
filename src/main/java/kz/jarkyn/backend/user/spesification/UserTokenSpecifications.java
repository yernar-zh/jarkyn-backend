package kz.jarkyn.backend.user.spesification;

import kz.jarkyn.backend.user.model.UserTokenEntity;
import kz.jarkyn.backend.user.model.UserTokenEntity_;
import org.springframework.data.jpa.domain.Specification;

public class UserTokenSpecifications {
    public static Specification<UserTokenEntity> token(String token) {
        return (root, query, cb) -> cb.equal(root.get(UserTokenEntity_.token), token);
    }
}