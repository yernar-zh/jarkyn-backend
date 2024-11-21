package kz.jarkyn.backend.counterparty.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.AbstractEntity_;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_summary_view")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class CustomerSummaryView extends CustomerEntity {
    @Column(name = "balance")
    private Integer balance;
    @Column(name = "first_sale_moment")
    private LocalDateTime firstSaleMoment;
    @Column(name = "last_sale_moment")
    private LocalDateTime lastSaleMoment;
    @Column(name = "total_sale_count")
    private Integer totalSaleCount;
    @Column(name = "total_sale_amount")
    private Integer totalSaleAmount;

    public Integer getBalance() {
        return balance;
    }

    public LocalDateTime getFirstSaleMoment() {
        return firstSaleMoment;
    }

    public LocalDateTime getLastSaleMoment() {
        return lastSaleMoment;
    }

    public Integer getTotalSaleCount() {
        return totalSaleCount;
    }

    public Integer getTotalSaleAmount() {
        return totalSaleAmount;
    }
}
