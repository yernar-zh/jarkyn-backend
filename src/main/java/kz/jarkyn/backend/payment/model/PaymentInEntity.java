package kz.jarkyn.backend.payment.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.account.model.AccountEntity;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.document.model.DocumentEntity;

@Entity
@Table(name = "payment_in")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class PaymentInEntity extends DocumentEntity {
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
    @Enumerated(EnumType.STRING)
    private PaymentInState state;

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public PaymentInState getState() {
        return state;
    }

    public void setState(PaymentInState state) {
        this.state = state;
    }
}
