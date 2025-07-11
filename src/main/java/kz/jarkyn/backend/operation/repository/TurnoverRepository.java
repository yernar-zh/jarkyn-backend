package kz.jarkyn.backend.operation.repository;

import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.warehouse.model.GoodEntity;
import kz.jarkyn.backend.operation.mode.TurnoverEntity;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface TurnoverRepository extends JpaRepository<TurnoverEntity, UUID> {
    List<TurnoverEntity> findByDocument(DocumentEntity document);
    @Query("""
            SELECT trv
            FROM TurnoverEntity trv
            WHERE (trv.good, trv.moment) IN (
                SELECT s_trv.good, MAX(s_trv.moment) FROM TurnoverEntity s_trv
                WHERE s_trv.warehouse = :warehouse
                AND s_trv.good IN :goods
                AND s_trv.moment < :moment
                GROUP BY s_trv.good
              )
            """)
    List<TurnoverEntity> findLastByGoodAndMoment(
            @Param("warehouse") WarehouseEntity warehouse,
            @Param("goods") List<GoodEntity> goods,
            @Param("moment") Instant moment
    );


    List<TurnoverEntity> findByWarehouseAndGoodAndMomentGreaterThanEqual(
            WarehouseEntity warehouse, GoodEntity good, Instant moment);
}
