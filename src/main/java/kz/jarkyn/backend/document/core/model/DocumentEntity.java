package kz.jarkyn.backend.document.core.model;


import kz.jarkyn.backend.core.model.AbstractEntity;
import jakarta.persistence.*;
import kz.jarkyn.backend.counterparty.model.CounterpartyEntity;
import kz.jarkyn.backend.counterparty.model.WarehouseEntity;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "document")
@Inheritance(strategy = InheritanceType.JOINED)
public class DocumentEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;
    @ManyToOne
    @JoinColumn(name = "counterparty_id")
    private CounterpartyEntity counterparty;
    private String name;
    private LocalDateTime moment;
    private BigDecimal amount;
    private String comment;
    private Boolean deleted;
    private Boolean commited;

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
