package kz.jarkyn.backend.repository;

import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.good.GoodAttributeEntity;
import kz.jarkyn.backend.model.good.GoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoodAttributeRepository extends JpaRepository<GoodAttributeEntity, UUID> {
    Optional<GoodAttributeEntity> findByGoodAndAttribute(GoodEntity good, AttributeEntity attribute);
}