package kz.jarkyn.backend.document.sale.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "sale_summary")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class SaleSummaryEntity extends SaleEntity {
    @Column(name = "balance")
    private Integer balance;
    @Column(name = "first_sale")
    private LocalDateTime firstSale;
    @Column(name = "last_sale")
    private LocalDateTime lastSale;
    private Integer totalSaleCount;
    private Integer totalSaleAmount;
    @Column(name = "shipment_moment")
    private LocalDateTime shipmentMoment;
    @Enumerated(EnumType.STRING)
    private SaleState state;

    public LocalDateTime getShipmentMoment() {
        return shipmentMoment;
    }

    public void setShipmentMoment(LocalDateTime shipmentMoment) {
        this.shipmentMoment = shipmentMoment;
    }

    public SaleState getState() {
        return state;
    }

    public void setState(SaleState state) {
        this.state = state;
    }
}
