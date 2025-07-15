package kz.jarkyn.backend.operation.mode;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.warehouse.model.GoodEntity;


import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "turnover")
public class TurnoverEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentEntity document;
    @ManyToOne
    @JoinColumn(name = "good_id")
    private GoodEntity good;
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;
    private Instant moment;
    private Integer quantity;
    @Column(name = "costPrice_per_unit")
    private BigDecimal costPricePerUnit;
    private Integer remain;
    @ManyToOne
    @JoinColumn(name = "last_inflow_id")
    private TurnoverEntity lastInflow;
    @Column(name = "last_inflow_used_quantity")
    private Integer lastInflowUsedQuantity;

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public GoodEntity getGood() {
        return good;
    }

    public void setGood(GoodEntity good) {
        this.good = good;
    }

    public WarehouseEntity getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(WarehouseEntity warehouse) {
        this.warehouse = warehouse;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getRemain() {
        return remain;
    }

    public void setRemain(Integer remain) {
        this.remain = remain;
    }

    public BigDecimal getCostPricePerUnit() {
        return costPricePerUnit;
    }

    public void setCostPricePerUnit(BigDecimal costPricePerUnit) {
        this.costPricePerUnit = costPricePerUnit;
    }

    public TurnoverEntity getLastInflow() {
        return lastInflow;
    }

    public void setLastInflow(TurnoverEntity lastInflow) {
        this.lastInflow = lastInflow;
    }

    public Integer getLastInflowUsedQuantity() {
        return lastInflowUsedQuantity;
    }

    public void setLastInflowUsedQuantity(Integer lastInflowUsedQuantity) {
        this.lastInflowUsedQuantity = lastInflowUsedQuantity;
    }
}
