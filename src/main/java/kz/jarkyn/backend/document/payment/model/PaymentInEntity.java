package kz.jarkyn.backend.document.payment.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

@Entity
@Table(name = "payment_in")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class PaymentInEntity extends DocumentEntity {
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }
}
