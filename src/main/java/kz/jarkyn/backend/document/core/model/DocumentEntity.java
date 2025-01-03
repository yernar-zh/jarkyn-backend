package kz.jarkyn.backend.document.core.model;


import kz.jarkyn.backend.core.model.AbstractEntity;
import jakarta.persistence.*;
import kz.jarkyn.backend.counterparty.model.CounterpartyEntity;
import kz.jarkyn.backend.counterparty.model.Currency;
import kz.jarkyn.backend.counterparty.model.OrganizationEntity;
import kz.jarkyn.backend.counterparty.model.WarehouseEntity;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "document")
@Inheritance(strategy = InheritanceType.JOINED)
public class DocumentEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organization;
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;
    @ManyToOne
    @JoinColumn(name = "counterparty_id")
    private CounterpartyEntity counterparty;
    private String name;
    private LocalDateTime moment;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private BigDecimal exchangeRate;
    private BigDecimal amount;
    private String comment;
    private Boolean deleted;
    private Boolean commited;

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

    public LocalDateTime getMoment() {
        return moment;
    }

    public void setMoment(LocalDateTime moment) {
        this.moment = moment;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
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
