package kz.jarkyn.backend.model.document.payment;


import jakarta.persistence.*;
import kz.jarkyn.backend.model.common.AbstractEntity;
import kz.jarkyn.backend.model.document.sale.SaleEntity;

@Entity
@Table(name = "payment_purpose")
public class PaymentInForSaleEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private PaymentInEntity paymentIn;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private SaleEntity sale;

    private Integer amount;

    public PaymentInEntity getPaymentIn() {
        return paymentIn;
    }

    public void setPaymentIn(PaymentInEntity paymentIn) {
        this.paymentIn = paymentIn;
    }

    public SaleEntity getSale() {
        return sale;
    }

    public void setSale(SaleEntity sale) {
        this.sale = sale;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
