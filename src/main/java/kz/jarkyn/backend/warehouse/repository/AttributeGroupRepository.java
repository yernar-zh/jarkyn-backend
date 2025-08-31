package kz.jarkyn.backend.warehouse.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.warehouse.model.AttributeGroupEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeGroupRepository extends AppRepository<AttributeGroupEntity> {
}