package kz.jarkyn.backend.document.sale.repository;

import jakarta.persistence.Tuple;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<SaleEntity, UUID> {
    @Query("""
                SELECT MIN(sle.moment) AS firstSaleDate,
                       MAX(sle.moment) AS lastSaleDate,
                       COUNT(sle) AS totalSaleCount,
                       SUM(sle.amount) AS totalSaleAmount
                FROM SaleEntity sle
                WHERE sle.customer = :customer
                AND sle.state = kz.jarkyn.backend.document.sale.model.SaleState.SHIPPED
            """)
    Tuple findCustomerInfo(@Param("customer") CustomerEntity customer);

}