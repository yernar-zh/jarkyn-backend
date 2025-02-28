package kz.jarkyn.backend.party.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.ReferenceEntity;
import kz.jarkyn.backend.global.model.CurrencyEntity;

@Entity
@Table(name = "account")
public class AccountEntity extends ReferenceEntity {
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;
    @ManyToOne
    @JoinColumn(name = "counterparty_id")
    private PartyEntity counterparty;
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
