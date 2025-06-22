package kz.jarkyn.backend.document.sale.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity;


import java.time.Instant;
@Entity
@Table(name = "sale")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class SaleEntity extends DocumentEntity {
    @Column(name = "shipment_moment")
    private Instant shipmentMoment;
    @Enumerated(EnumType.STRING)
    private SaleState state;

    public Instant getShipmentMoment() {
        return shipmentMoment;
    }

    public void setShipmentMoment(Instant shipmentMoment) {
        this.shipmentMoment = shipmentMoment;
    }

    public SaleState getState() {
        return state;
    }

    public void setState(SaleState state) {
        this.state = state;
    }
}
