package kz.jarkyn.backend.good.repository;

import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.good.model.GoodAttributeEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoodAttributeRepository extends JpaRepository<GoodAttributeEntity, UUID> {
    Optional<GoodAttributeEntity> findByGoodAndAttribute(GoodEntity good, AttributeEntity attribute);
}