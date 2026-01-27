package kz.jarkyn.backend.document.core.model;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.audit.config.AuditIgnore;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.good.model.GoodEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "item")
public class ItemEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentEntity document;
    @ManyToOne
    @JoinColumn(name = "good_id")
    private GoodEntity good;
    private Integer quantity;
    private BigDecimal price;
    @AuditIgnore
    private Integer position;

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
