package kz.jarkyn.backend.repository;

import kz.jarkyn.backend.model.document.payment.PaymentInForSaleEntity;
import kz.jarkyn.backend.model.document.sale.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentInForSaleRepository extends JpaRepository<PaymentInForSaleEntity, UUID> {
    List<PaymentInForSaleEntity> findBySale(SaleEntity sale);
}