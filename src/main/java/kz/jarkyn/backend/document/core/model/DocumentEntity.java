package kz.jarkyn.backend.document.core.model;


import kz.jarkyn.backend.core.model.AbstractEntity;
import jakarta.persistence.*;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.WarehouseEntity;


import java.time.LocalDateTime;

@Entity
@Table(name = "document")
@Inheritance(strategy = InheritanceType.JOINED)
public class DocumentEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
    private String name;
    private LocalDateTime moment;
    private Integer amount;
    private String comment;
    private Boolean deleted;

    public WarehouseEntity getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(WarehouseEntity warehouse) {
        this.warehouse = warehouse;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
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
}
