package kz.jarkyn.backend.operation.repository;

import kz.jarkyn.backend.party.model.AccountEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.operation.mode.CashFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CashFlowRepository extends JpaRepository<CashFlowEntity, UUID> {
    @Query("""
            SELECT cfl
            FROM CashFlowEntity cfl
            WHERE cfl.account = :account
            AND cfl.moment < :moment
            ORDER BY cfl.moment DESC, cfl.lastModifiedAt DESC
            LIMIT 1
            """)
    Optional<CashFlowEntity> findLastByAccountAndMoment(
            @Param("account") AccountEntity account,
            @Param("moment") Instant moment
    );
    List<CashFlowEntity> findByDocument(DocumentEntity document);
    List<CashFlowEntity> findByAccountAndMomentGreaterThanEqual(AccountEntity account, Instant moment);
}
