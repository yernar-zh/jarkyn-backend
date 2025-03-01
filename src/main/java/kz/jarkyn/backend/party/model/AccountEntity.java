package kz.jarkyn.backend.party.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.reference.model.CurrencyEntity;

@Entity
@Table(name = "account")
public class AccountEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;
    @ManyToOne
    @JoinColumn(name = "counterparty_id")
    private PartyEntity counterparty;
    private String name;
    private String bank;
    private String giro;
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private CurrencyEntity currency;

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }

    public PartyEntity getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(PartyEntity counterparty) {
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

    public CurrencyEntity getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyEntity currency) {
        this.currency = currency;
    }
}
