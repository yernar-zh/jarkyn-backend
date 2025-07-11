package kz.jarkyn.backend.warehouse.repository;

import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, UUID> {
    List<WarehouseEntity> findByArchived(Boolean archived);
}