package kz.jarkyn.backend.counterparty.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.document.sale.model.SaleEntity;

import java.time.LocalDateTime;

@Entity
//@Table(name = "customer_summary")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class CustomerSummaryEntity extends SaleEntity {
    @Column(name = "balance")
    private Integer balance;
    @Column(name = "first_sale")
    private LocalDateTime firstSale;
    @Column(name = "last_sale")
    private LocalDateTime lastSale;
    @Column(name = "total_sale_count")
    private Integer totalSaleCount;
    @Column(name = "total_sale_amount")
    private Integer totalSaleAmount;

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public LocalDateTime getFirstSale() {
        return firstSale;
    }

    public void setFirstSale(LocalDateTime firstSale) {
        this.firstSale = firstSale;
    }

    public LocalDateTime getLastSale() {
        return lastSale;
    }

    public void setLastSale(LocalDateTime lastSale) {
        this.lastSale = lastSale;
    }

    public Integer getTotalSaleCount() {
        return totalSaleCount;
    }

    public void setTotalSaleCount(Integer totalSaleCount) {
        this.totalSaleCount = totalSaleCount;
    }

    public Integer getTotalSaleAmount() {
        return totalSaleAmount;
    }

    public void setTotalSaleAmount(Integer totalSaleAmount) {
        this.totalSaleAmount = totalSaleAmount;
    }
}
