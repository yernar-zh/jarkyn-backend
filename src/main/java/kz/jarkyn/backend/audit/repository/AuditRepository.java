package kz.jarkyn.backend.audit.repository;

import kz.jarkyn.backend.audit.model.AuditEntity;
import kz.jarkyn.backend.core.repository.AppRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditRepository extends AppRepository<AuditEntity> {
}