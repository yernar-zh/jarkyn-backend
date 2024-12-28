package kz.jarkyn.backend.counterparty.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;

@Entity
@Table(name = "account")
public class AccountEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;
    @ManyToOne
    @JoinColumn(name = "counterparty_id")
    private CounterpartyEntity counterparty;
    private String name;
    private String bank;
    private String giro;
    @Enumerated(EnumType.STRING)
    private Currency currency;

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }

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

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
