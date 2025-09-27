package kz.jarkyn.backend.document.bind.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.document.bind.model.BindDocumentEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface BindDocumentRepository extends AppRepository<BindDocumentEntity> {
}