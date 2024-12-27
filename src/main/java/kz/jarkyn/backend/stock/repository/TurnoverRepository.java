package kz.jarkyn.backend.stock.repository;

import jakarta.persistence.Tuple;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.stock.mode.TurnoverEntity;
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
