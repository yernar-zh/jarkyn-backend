package kz.jarkyn.backend.reference.repository;

import kz.jarkyn.backend.reference.model.CurrencyEntity;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, UUID> {
}