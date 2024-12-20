package kz.jarkyn.backend.document.payment.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_out")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class PaymentOutEntity extends DocumentEntity {
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
    @JoinColumn(name = "exchange_rate")
    private BigDecimal exchangeRate;

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
