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
    @Column(name = "places_quantity")
    private Integer placesQuantity;
    @Column(name = "items_amount")
    private Integer itemsAmount;
    @Column(name = "discount_amount")
    private Integer discountAmount;
    @Enumerated(EnumType.STRING)
    private SaleState state;

    public LocalDateTime getShipmentMoment() {
        return shipmentMoment;
    }

    public void setShipmentMoment(LocalDateTime shipmentMoment) {
        this.shipmentMoment = shipmentMoment;
    }

    public Integer getPlacesQuantity() {
        return placesQuantity;
    }

    public void setPlacesQuantity(Integer placesQuantity) {
        this.placesQuantity = placesQuantity;
    }

    public Integer getItemsAmount() {
        return itemsAmount;
    }

    public void setItemsAmount(Integer itemsAmount) {
        this.itemsAmount = itemsAmount;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public SaleState getState() {
        return state;
    }

    public void setState(SaleState state) {
        this.state = state;
    }
}
