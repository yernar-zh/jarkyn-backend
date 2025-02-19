package kz.jarkyn.backend.party.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;

@Entity
@Table(name = "party")
@Inheritance(strategy = InheritanceType.JOINED)
public class PartyEntity extends AbstractEntity {
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
