package kz.jarkyn.backend.model.counterparty;


import jakarta.persistence.*;
import kz.jarkyn.backend.model.common.AbstractEntity;
import kz.jarkyn.backend.model.document.DocumentEntity;

@Entity
@Table(name = "counterparty")
public class CounterpartyEntity extends AbstractEntity {
    private String name;
    @Enumerated(EnumType.STRING)
    private CounterpartyType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CounterpartyType getType() {
        return type;
    }

    public void setType(CounterpartyType type) {
        this.type = type;
    }
}
