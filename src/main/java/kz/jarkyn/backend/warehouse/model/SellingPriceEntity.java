package kz.jarkyn.backend.warehouse.model;

import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "selling_price")
public class SellingPriceEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "good_id")
    private GoodEntity good;
    private Integer quantity;
    @Column(name = "val")
    private BigDecimal value;

    public GoodEntity getGood() {
        return good;
    }

    public void setGood(GoodEntity good) {
        this.good = good;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
