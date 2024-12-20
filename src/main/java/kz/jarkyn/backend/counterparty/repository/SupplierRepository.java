package kz.jarkyn.backend.counterparty.repository;

import jakarta.persistence.Tuple;
import kz.jarkyn.backend.counterparty.model.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, UUID> {
    @Query("""
            SELECT
                MIN(spl.moment) as firstSupplyMoment,
                MAX(spl.moment) as lastSupplyMoment,
                COUNT(spl) as totalSupplyCount,
                COALESCE(SUM(spl.amount),0) as totalSupplyAmount
            FROM SupplyEntity spl
            WHERE spl.counterparty = :supplier
                AND spl.commited = TRUE
            """)
    Tuple findSupplyInfo(@Param("supplier") SupplierEntity supplier);
}