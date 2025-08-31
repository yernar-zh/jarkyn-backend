package kz.jarkyn.backend.document.payment.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaidDocumentRepository extends AppRepository<PaidDocumentEntity> {
    List<PaidDocumentEntity> findByPayment(DocumentEntity payment);

    @Query("SELECT pd FROM PaidDocumentEntity pd " +
           "WHERE pd.document = :document ")
    List<PaidDocumentEntity> findByDocument(@Param("document") DocumentEntity document);
}