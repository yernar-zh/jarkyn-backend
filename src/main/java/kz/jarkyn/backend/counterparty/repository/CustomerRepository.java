package kz.jarkyn.backend.counterparty.repository;

import kz.jarkyn.backend.core.repository.PageSpecificationExecutor;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.dto.CustomerDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID>, PageSpecificationExecutor<CustomerEntity> {
    @Query("""
                SELECT
                    ctr.id,
                    ctr.name,
                    ctr.phoneNumber,
                    ctr.shippingAddress,
                    ctr.discount,
                    act.balance,
                    MIN(sle.moment) as firstSaleMoment,
                    MAX(sle.moment) as lastSaleMoment,
                    COUNT(sle) as totalSaleCount,
                    SUM(sle.amount) as totalSaleAmount,
                    ctr.discount as discount
                FROM CustomerEntity ctr
                LEFT JOIN AccountEntity act ON ctr = act.counterparty
                LEFT JOIN SaleEntity sle ON ctr = sle.customer
                WHERE sle.state = kz.jarkyn.backend.document.sale.model.SaleState.SHIPPED
                GROUP BY ctr
            """)
    List<CustomerDto> findDtoAll();
}