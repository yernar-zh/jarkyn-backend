package kz.jarkyn.backend.model.good;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.model.common.AbstractEntity;

@Entity
@Table(name = "package")
public class PackageEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "product_id")
    private GoodEntity product;
    private Integer quantity;
    private Integer price;

    public GoodEntity getProduct() {
        return product;
    }

    public void setProduct(GoodEntity product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
