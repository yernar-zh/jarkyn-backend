package kz.jarkyn.backend.document.payment.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.document.payment.model.PaymentOutEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentOutRepository extends AppRepository<PaymentOutEntity> {
}