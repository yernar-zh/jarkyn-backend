package kz.jarkyn.backend.user.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.user.model.SessionEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends AppRepository<SessionEntity> {
}