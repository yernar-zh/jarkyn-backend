package kz.jarkyn.backend.document.core.model;


import kz.jarkyn.backend.audit.config.IgnoreAudit;
import kz.jarkyn.backend.core.model.AbstractEntity;
import jakarta.persistence.*;
import kz.jarkyn.backend.party.model.AccountEntity;
import kz.jarkyn.backend.party.model.CounterpartyEntity;
import kz.jarkyn.backend.party.model.OrganizationEntity;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;


import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "document")
@Inheritance(strategy = InheritanceType.JOINED)
public class DocumentEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "type_id")
    private DocumentTypeEntity type;
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
    @ManyToOne
    @JoinColumn(name = "counterparty_id")
    private CounterpartyEntity counterparty;
    private String name;
    private Instant moment;
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private CurrencyEntity currency;
    @JoinColumn(name = "exchange_rate")
    private BigDecimal exchangeRate;
    private BigDecimal amount;
    private String comment;
    @IgnoreAudit
    private Boolean deleted;
    @IgnoreAudit
    private Boolean commited;

    public DocumentTypeEntity getType() {
        return type;
    }

    public void setType(DocumentTypeEntity type) {
        this.type = type;
    }

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }

    public WarehouseEntity getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(WarehouseEntity warehouse) {
        this.warehouse = warehouse;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
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

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public CurrencyEntity getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyEntity currency) {
        this.currency = currency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getCommited() {
        return commited;
    }

    public void setCommited(Boolean commited) {
        this.commited = commited;
    }
}
