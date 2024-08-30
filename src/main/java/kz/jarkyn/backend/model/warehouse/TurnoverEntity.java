package kz.jarkyn.backend.model.warehouse;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.model.common.AbstractEntity;
import kz.jarkyn.backend.model.document.DocumentEntity;

@Entity
@Table(name = "turnover")
public class TurnoverEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "stock_id")
    private StockEntity stock;
    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentEntity document;
    private Integer quantity;

    public StockEntity getStock() {
        return stock;
    }

    public void setStock(StockEntity stock) {
        this.stock = stock;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
