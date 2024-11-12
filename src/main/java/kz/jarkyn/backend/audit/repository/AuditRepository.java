package kz.jarkyn.backend.audit.repository;

import kz.jarkyn.backend.audit.model.enity.AuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntity, UUID> {
    @Query("""
                SELECT aut FROM AuditEntity aut
                WHERE aut.entityId = :entityId
                AND aut.fieldName = :fieldName
                ORDER BY aut.moment DESC
                LIMIT 1
            """)
    AuditEntity getLastByEntityIdAndFieldName(
            @Param("entityId") UUID entityId,
            @Param("fieldName") String fieldName);
}