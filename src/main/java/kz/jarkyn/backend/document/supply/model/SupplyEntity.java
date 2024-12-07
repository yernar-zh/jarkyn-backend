package kz.jarkyn.backend.document.supply.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

@Entity
@Table(name = "supply")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class SupplyEntity extends DocumentEntity {
    private Integer exchangeRate;
    @Enumerated(EnumType.STRING)
    private SupplyState state;

    public Integer getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Integer exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public SupplyState getState() {
        return state;
    }

    public void setState(SupplyState state) {
        this.state = state;
    }
}
