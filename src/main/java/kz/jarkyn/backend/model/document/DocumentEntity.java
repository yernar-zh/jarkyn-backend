package kz.jarkyn.backend.model.document;


import kz.jarkyn.backend.model.common.AbstractEntity;
import jakarta.persistence.*;
import kz.jarkyn.backend.model.counterparty.CounterpartyEntity;
import kz.jarkyn.backend.model.warehouse.WarehouseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "document")
public class DocumentEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CounterpartyEntity customer;
    private String name;
    private LocalDateTime moment;
    private String comment;
    private DocumentType type;
    private Boolean deleted;

    public WarehouseEntity getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(WarehouseEntity warehouse) {
        this.warehouse = warehouse;
    }

    public CounterpartyEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CounterpartyEntity customer) {
        this.customer = customer;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
