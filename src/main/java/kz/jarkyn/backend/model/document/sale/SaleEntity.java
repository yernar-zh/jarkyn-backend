package kz.jarkyn.backend.model.document.sale;


import jakarta.persistence.*;
import kz.jarkyn.backend.model.common.AbstractEntity_;
import kz.jarkyn.backend.model.document.common.DocumentEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "sale")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class SaleEntity extends DocumentEntity {
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
