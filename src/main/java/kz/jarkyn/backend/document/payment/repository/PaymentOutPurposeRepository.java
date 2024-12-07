package kz.jarkyn.backend.document.payment.repository;

import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.payment.model.PaymentOutPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentOutPurposeRepository extends JpaRepository<PaymentOutPurpose, UUID> {
    List<PaymentOutPurpose> findByDocument(DocumentEntity document);
}