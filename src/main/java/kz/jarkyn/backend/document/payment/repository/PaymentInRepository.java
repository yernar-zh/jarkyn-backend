package kz.jarkyn.backend.document.payment.repository;

import kz.jarkyn.backend.document.payment.model.PaymentInEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentInRepository extends JpaRepository<PaymentInEntity, UUID> {
}