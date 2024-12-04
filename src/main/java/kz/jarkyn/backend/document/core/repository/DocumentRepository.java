package kz.jarkyn.backend.document.core.repository;

import kz.jarkyn.backend.document.core.model.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
    @Query("""
                SELECT dmt.name FROM DocumentEntity dmt
                WHERE dmt.name like concat(:namePrefix, '-%')
                ORDER BY dmt.name DESC
                LIMIT 1
            """)
    String getLastNameByNamePrefix(@Param("namePrefix") String namePrefix);
}