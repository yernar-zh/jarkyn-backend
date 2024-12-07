package kz.jarkyn.backend.document.payment.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

@Entity
@Table(name = "payment_out")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class PaymentOutEntity extends DocumentEntity {
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
    @Enumerated(EnumType.STRING)
    private PaymentOutState state;

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public PaymentOutState getState() {
        return state;
    }

    public void setState(PaymentOutState state) {
        this.state = state;
    }
}
