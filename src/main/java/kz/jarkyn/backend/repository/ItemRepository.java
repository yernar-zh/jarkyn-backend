package kz.jarkyn.backend.repository;

import kz.jarkyn.backend.model.document.common.DocumentEntity;
import kz.jarkyn.backend.model.document.common.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, UUID> {
    List<ItemEntity> findByDocument(DocumentEntity document);
}