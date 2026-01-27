package kz.jarkyn.backend.good.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.good.model.AttributeGroupEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeGroupRepository extends AppRepository<AttributeGroupEntity> {
}