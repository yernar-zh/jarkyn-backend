package kz.jarkyn.backend.party.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.AbstractEntity;

@Entity
@Table(name = "warehouse")
public class WarehouseEntity extends AbstractEntity {
    private String name;
    private Boolean archived;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }
}
