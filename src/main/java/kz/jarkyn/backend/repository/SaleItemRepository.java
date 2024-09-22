package kz.jarkyn.backend.repository;

import kz.jarkyn.backend.model.document.sale.SaleEntity;
import kz.jarkyn.backend.model.document.sale.SaleItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItemEntity, UUID> {
}