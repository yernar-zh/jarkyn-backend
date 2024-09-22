package kz.jarkyn.backend.model.warehouse;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.model.common.AbstractEntity;
import kz.jarkyn.backend.model.document.DocumentEntity;
import kz.jarkyn.backend.model.good.GoodEntity;

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
}
