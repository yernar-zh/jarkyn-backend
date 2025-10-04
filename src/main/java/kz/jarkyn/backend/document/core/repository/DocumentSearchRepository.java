package kz.jarkyn.backend.document.core.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.document.core.model.DocumentSearchEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentSearchRepository extends AppRepository<DocumentSearchEntity> {
}