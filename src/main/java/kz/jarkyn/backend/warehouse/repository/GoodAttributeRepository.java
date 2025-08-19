package kz.jarkyn.backend.warehouse.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.warehouse.model.AttributeEntity;
import kz.jarkyn.backend.warehouse.model.GoodAttributeEntity;
import kz.jarkyn.backend.warehouse.model.GoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoodAttributeRepository extends AppRepository<GoodAttributeEntity> {
    Optional<GoodAttributeEntity> findByGoodAndAttribute(GoodEntity good, AttributeEntity attribute);
}