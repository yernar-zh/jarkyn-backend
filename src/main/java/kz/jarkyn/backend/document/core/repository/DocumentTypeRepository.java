package kz.jarkyn.backend.document.core.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.document.core.model.DocumentTypeEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentTypeRepository extends AppRepository<DocumentTypeEntity> {
    Optional<DocumentTypeEntity> findByCode(String name);
}