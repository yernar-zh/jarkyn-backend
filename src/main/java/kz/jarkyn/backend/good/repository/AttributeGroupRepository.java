package kz.jarkyn.backend.good.repository;

import kz.jarkyn.backend.good.model.AttributeGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttributeGroupRepository extends JpaRepository<AttributeGroupEntity, UUID> {
}