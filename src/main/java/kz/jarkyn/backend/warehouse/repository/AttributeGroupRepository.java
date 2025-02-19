package kz.jarkyn.backend.warehouse.repository;

import kz.jarkyn.backend.warehouse.model.AttributeGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttributeGroupRepository extends JpaRepository<AttributeGroupEntity, UUID> {
}