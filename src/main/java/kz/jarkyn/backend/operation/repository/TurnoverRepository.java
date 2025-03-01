package kz.jarkyn.backend.operation.repository;

import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.warehouse.model.GoodEntity;
import kz.jarkyn.backend.operation.mode.TurnoverEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TurnoverRepository extends JpaRepository<TurnoverEntity, UUID> {
    List<TurnoverEntity> findByDocument(DocumentEntity document);
    @Query("""
            SELECT trv
            FROM TurnoverEntity trv
            WHERE trv.good IN :goods
            AND trv.moment = (
                SELECT max(s_trv.moment) FROM TurnoverEntity s_trv
                WHERE s_trv.good = trv.good)
            """)
    List<TurnoverEntity> findLastByGood(@Param("goods") List<GoodEntity> goods);
}
