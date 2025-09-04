package kz.jarkyn.backend.user.spesification;

import kz.jarkyn.backend.user.model.SessionEntity;
import kz.jarkyn.backend.user.model.SessionEntity_;
import kz.jarkyn.backend.user.model.UserEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class SessionSpecifications {
    public static Specification<SessionEntity> token(String token) {
        return (root, query, cb) -> cb.equal(root.get(SessionEntity_.refreshTokenHash), token);
    }

    public static Specification<SessionEntity> user(UserEntity user) {
        return (root, query, cb) -> cb.equal(root.get(SessionEntity_.user), user);
    }

    public static Specification<SessionEntity> refreshTokenHash(String refreshTokenHash) {
        return (root, query, cb) -> cb.equal(root.get(SessionEntity_.refreshTokenHash), refreshTokenHash);
    }
}