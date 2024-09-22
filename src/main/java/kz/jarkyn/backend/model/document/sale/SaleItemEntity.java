package kz.jarkyn.backend.model.document.sale;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.model.common.AbstractEntity;
import kz.jarkyn.backend.model.good.GoodEntity;

@Entity
@Table(name = "sale_product")
public class SaleItemEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "sale_id")
    private SaleEntity sale;
    private Integer position;
    @ManyToOne
    @JoinColumn(name = "good_id")
    private GoodEntity good;
    private Integer price;
    private Integer quantity;

    public SaleEntity getSale() {
        return sale;
    }

    public void setSale(SaleEntity sale) {
        this.sale = sale;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public GoodEntity getGood() {
        return good;
    }

    public void setGood(GoodEntity good) {
        this.good = good;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
