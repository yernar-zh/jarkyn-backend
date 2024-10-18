package kz.jarkyn.backend.model.good;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.model.common.AbstractEntity;

@Entity
@Table(name = "selling_price")
public class SellingPriceEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "good_id")
    private GoodEntity good;
    private Integer quantity;
    private Integer value;

    public GoodEntity getGood() {
        return good;
    }

    public void setGood(GoodEntity product) {
        this.good = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return value;
    }

    public void setPrice(Integer price) {
        this.value = price;
    }
}
