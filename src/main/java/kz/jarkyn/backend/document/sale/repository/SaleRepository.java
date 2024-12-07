package kz.jarkyn.backend.document.sale.repository;

import kz.jarkyn.backend.document.sale.model.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<SaleEntity, UUID> {

}