package kz.jarkyn.backend.counterparty.repository;

import jakarta.persistence.Tuple;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    @Query("""
            SELECT
                MIN(sle.moment) as firstSaleMoment,
                MAX(sle.moment) as lastSaleMoment,
                COUNT(sle) as totalSaleCount,
                COALESCE(SUM(sle.amount),0) as totalSaleAmount
            FROM SaleEntity sle
            WHERE sle.counterparty = :customer
                AND sle.commited = TRUE
            """)
    Tuple findSaleInfo(@Param("customer") CustomerEntity customer);
}