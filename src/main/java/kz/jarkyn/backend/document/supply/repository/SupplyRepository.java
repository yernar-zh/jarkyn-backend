package kz.jarkyn.backend.document.supply.repository;

import kz.jarkyn.backend.document.supply.model.SupplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupplyRepository extends JpaRepository<SupplyEntity, UUID> {
}