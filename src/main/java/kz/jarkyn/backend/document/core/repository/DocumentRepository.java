package kz.jarkyn.backend.document.core.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.DocumentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentRepository extends AppRepository<DocumentEntity> {
}