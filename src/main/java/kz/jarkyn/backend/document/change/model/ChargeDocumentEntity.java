package kz.jarkyn.backend.document.change.model;


import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

import java.math.BigDecimal;

//@Entity
//@Table(name = "charge_document")
public class ChargeDocumentEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "charge_id")
    private OpexEntity charge;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    private BigDecimal amount;

    public OpexEntity getCharge() {
        return charge;
    }

    public void setCharge(OpexEntity charge) {
        this.charge = charge;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
