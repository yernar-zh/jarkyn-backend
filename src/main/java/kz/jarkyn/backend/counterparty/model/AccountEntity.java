package kz.jarkyn.backend.counterparty.model;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.AbstractEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
public class AccountEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "counterparty_id")
    private CounterpartyEntity counterparty;
    private String name;
    private BigDecimal balance;
    private String bank;
    private String giro;

    public CounterpartyEntity getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(CounterpartyEntity counterparty) {
        this.counterparty = counterparty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getGiro() {
        return giro;
    }

    public void setGiro(String giro) {
        this.giro = giro;
    }
}
