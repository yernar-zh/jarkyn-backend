package kz.jarkyn.backend.model.document.payment;


import jakarta.persistence.*;
import kz.jarkyn.backend.model.account.AccountEntity;
import kz.jarkyn.backend.model.common.AbstractEntity_;
import kz.jarkyn.backend.model.document.common.DocumentEntity;
import kz.jarkyn.backend.model.document.sale.SaleState;

@Entity
@Table(name = "payment_in")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class PaymentInEntity extends DocumentEntity {
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
    private Integer amount;
    @Enumerated(EnumType.STRING)
    private SaleState state;

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public SaleState getState() {
        return state;
    }

    public void setState(SaleState state) {
        this.state = state;
    }
}
