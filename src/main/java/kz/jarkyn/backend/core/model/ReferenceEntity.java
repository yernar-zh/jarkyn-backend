package kz.jarkyn.backend.core.model;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class ReferenceEntity extends AbstractEntity {
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
