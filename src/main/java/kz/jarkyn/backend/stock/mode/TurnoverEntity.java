package kz.jarkyn.backend.stock.mode;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.good.model.GoodEntity;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "turnover")
public class TurnoverEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentEntity document;
    @ManyToOne
    @JoinColumn(name = "good_id")
    private GoodEntity good;
    private LocalDateTime moment;
    private Integer quantity;
    private Integer remain;
    private BigDecimal costPrice;

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public GoodEntity getGood() {
        return good;
    }

    public void setGood(GoodEntity good) {
        this.good = good;
    }

    public LocalDateTime getMoment() {
        return moment;
    }

    public void setMoment(LocalDateTime moment) {
        this.moment = moment;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getRemain() {
        return remain;
    }

    public void setRemain(Integer remain) {
        this.remain = remain;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }
}
