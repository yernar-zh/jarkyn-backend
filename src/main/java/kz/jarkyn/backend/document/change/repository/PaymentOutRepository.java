package kz.jarkyn.backend.document.change.repository;

import kz.jarkyn.backend.document.payment.model.PaymentOutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentOutRepository extends JpaRepository<PaymentOutEntity, UUID> {
}