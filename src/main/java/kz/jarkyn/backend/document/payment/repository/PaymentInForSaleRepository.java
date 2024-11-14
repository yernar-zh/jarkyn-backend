package kz.jarkyn.backend.document.payment.repository;

import kz.jarkyn.backend.document.payment.model.PaymentInForSaleEntity;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentInForSaleRepository extends JpaRepository<PaymentInForSaleEntity, UUID> {
    List<PaymentInForSaleEntity> findBySale(SaleEntity sale);
}