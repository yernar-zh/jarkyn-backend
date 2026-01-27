package kz.jarkyn.backend.operation.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.operation.mode.TurnoverEntity;
import kz.jarkyn.backend.party.model.WarehouseEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TurnoverRepository extends AppRepository<TurnoverEntity> {
    @Query("""
            SELECT trv
            FROM TurnoverEntity trv
            WHERE trv.warehouse = :warehouse AND trv.good in :goods
            AND trv.moment = (
                SELECT MAX(s_trv.moment) FROM TurnoverEntity s_trv
                WHERE s_trv.good = trv.good AND s_trv.moment < :moment)
            AND trv.lastModifiedAt = (
                SELECT MAX(s_trv.lastModifiedAt) FROM TurnoverEntity s_trv
                WHERE s_trv.good = trv.good AND s_trv.moment = trv.moment)
            """)
    List<TurnoverEntity> findLastByMomentLessThan(
            @Param("warehouse") WarehouseEntity warehouse,
            @Param("goods") List<GoodEntity> goods,
            @Param("moment") Instant moment
    );

    @Query("""
            SELECT trv
            FROM TurnoverEntity trv
            WHERE trv.warehouse = :warehouse AND trv.good in :goods
            AND trv.moment = (
                SELECT MIN(s_trv.moment) FROM TurnoverEntity s_trv
                WHERE s_trv.good = trv.good AND s_trv.moment < :moment AND s_trv.quantity > 0)
            AND trv.lastModifiedAt = (
                SELECT MIN(s_trv.lastModifiedAt) FROM TurnoverEntity s_trv
                WHERE s_trv.good = trv.good AND s_trv.moment = trv.moment)
            """)
    List<TurnoverEntity> findFirstInflow(
            @Param("warehouse") WarehouseEntity warehouse,
            @Param("goods") List<GoodEntity> goods,
            @Param("moment") Instant moment
    );
}
