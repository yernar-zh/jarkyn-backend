package kz.jarkyn.backend.document.core.repository;

import kz.jarkyn.backend.document.core.model.DocumentTypeEntity;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentTypeEntity, UUID> {
    Optional<DocumentTypeEntity> findByCode(String name);
}