package kz.jarkyn.backend.document.change.repository;

import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaidDocumentRepository extends JpaRepository<PaidDocumentEntity, UUID> {
    List<PaidDocumentEntity> findByPayment(DocumentEntity payment);

    @Query("SELECT pd FROM PaidDocumentEntity pd " +
           "WHERE pd.document = :document ")
    List<PaidDocumentEntity> findByDocument(@Param("document") DocumentEntity document);
}