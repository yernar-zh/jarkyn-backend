package kz.jarkyn.backend.document.payment.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.document.payment.model.PaymentInEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentInRepository extends AppRepository<PaymentInEntity> {
}