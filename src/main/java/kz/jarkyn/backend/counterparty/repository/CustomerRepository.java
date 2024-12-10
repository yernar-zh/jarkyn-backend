package kz.jarkyn.backend.counterparty.repository;

import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.dto.CustomerListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    @Query("""
            SELECT
                ctr.id as id,
                ctr.name as name,
                ctr.phoneNumber as phoneNumber,
                ctr.shippingAddress as shippingAddress,
                ctr.discount as discount,
                act.balance as balance,
                MIN(sle.moment) as firstSaleMoment,
                MAX(sle.moment) as lastSaleMoment,
                COUNT(sle) as totalSaleCount,
                COALESCE(SUM(sle.amount),0) as totalSaleAmount
            FROM CustomerEntity ctr
            LEFT JOIN AccountEntity act ON ctr = act.counterparty
            LEFT JOIN SaleEntity sle ON ctr = sle.counterparty
                AND sle.state = kz.jarkyn.backend.document.sale.model.SaleState.SHIPPED
            GROUP BY ctr.id, ctr.name, ctr.phoneNumber, ctr.shippingAddress, ctr.discount, act.balance
            """)
    List<CustomerListResponse> findAllResponse();
}