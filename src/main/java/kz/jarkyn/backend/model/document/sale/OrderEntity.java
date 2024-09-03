package kz.jarkyn.backend.model.document.sale;


import jakarta.persistence.*;
import kz.jarkyn.backend.model.common.AbstractEntity;
import kz.jarkyn.backend.model.document.DocumentEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class OrderEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentEntity document;
    @Column(name = "shipment_moment")
    private LocalDateTime shipmentMoment;
    @Column(name = "places_quantity")
    private Integer placesQuantity;
    @Column(name = "products_total_amount")
    private Integer productsTotalAmount;
    @Column(name = "discount_amount")
    private Integer discountAmount;
    @Column(name = "total_amount")
    private Integer totalAmount;
    @Enumerated(EnumType.STRING)
    private OrderState state;

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

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

    public Integer getProductsTotalAmount() {
        return productsTotalAmount;
    }

    public void setProductsTotalAmount(Integer productsTotalAmount) {
        this.productsTotalAmount = productsTotalAmount;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }
}
