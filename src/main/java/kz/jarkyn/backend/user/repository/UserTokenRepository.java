package kz.jarkyn.backend.user.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.user.model.UserTokenEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTokenRepository extends AppRepository<UserTokenEntity> {
}