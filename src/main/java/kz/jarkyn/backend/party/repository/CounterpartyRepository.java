package kz.jarkyn.backend.party.repository;

import jakarta.persistence.Tuple;
import kz.jarkyn.backend.party.model.CounterpartyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface CounterpartyRepository extends JpaRepository<CounterpartyEntity, UUID> {
    @Query("""
            SELECT
                MIN(sle.moment) as firstSaleMoment,
                MAX(sle.moment) as lastSaleMoment,
                COUNT(sle) as totalSaleCount,
                COALESCE(SUM(sle.amount),0) as totalSaleAmount
            FROM SaleEntity sle
            WHERE sle.counterparty = :counterparty
                AND sle.commited = TRUE
            """)
    Tuple findSaleInfo(@Param("counterparty") CounterpartyEntity counterparty);
}