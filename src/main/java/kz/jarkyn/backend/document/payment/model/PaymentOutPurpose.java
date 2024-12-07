package kz.jarkyn.backend.document.payment.model;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

@Entity
@Table(name = "payment_out_purpose")
public class PaymentOutPurpose extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "payment_out")
    private PaymentOutEntity paymentOut;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    private Integer amount;

    public PaymentOutEntity getPaymentOut() {
        return paymentOut;
    }

    public void setPaymentOut(PaymentOutEntity paymentOut) {
        this.paymentOut = paymentOut;
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
