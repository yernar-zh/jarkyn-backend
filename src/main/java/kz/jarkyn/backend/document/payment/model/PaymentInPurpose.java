package kz.jarkyn.backend.document.payment.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

@Entity
@Table(name = "payment_in_purpose")
public class PaymentInPurpose extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private PaymentInEntity paymentIn;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    private Integer amount;

    public PaymentInEntity getPaymentIn() {
        return paymentIn;
    }

    public void setPaymentIn(PaymentInEntity paymentIn) {
        this.paymentIn = paymentIn;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
