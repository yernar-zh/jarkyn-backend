package kz.jarkyn.backend.model.counterparty;


import jakarta.persistence.*;
import kz.jarkyn.backend.model.common.AbstractEntity;

@Entity
@Table(name = "counterparty")
@Inheritance(strategy = InheritanceType.JOINED)
public class CounterpartyEntity extends AbstractEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
