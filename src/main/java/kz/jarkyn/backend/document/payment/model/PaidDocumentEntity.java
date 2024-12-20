package kz.jarkyn.backend.document.payment.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "paid_document")
public class PaidDocumentEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private DocumentEntity payment;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    private BigDecimal amount;

    public DocumentEntity getPayment() {
        return payment;
    }

    public void setPayment(DocumentEntity payment) {
        this.payment = payment;
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
