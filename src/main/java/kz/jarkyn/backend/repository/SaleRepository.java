package kz.jarkyn.backend.repository;

import kz.jarkyn.backend.model.document.sale.SaleEntity;
import kz.jarkyn.backend.model.good.GoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<SaleEntity, UUID> {
}