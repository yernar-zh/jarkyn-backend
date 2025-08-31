package kz.jarkyn.backend.user.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.user.model.RoleEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends AppRepository<RoleEntity> {
}