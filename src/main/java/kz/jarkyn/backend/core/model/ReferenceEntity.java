package kz.jarkyn.backend.core.model;

import jakarta.persistence.*;
import kz.jarkyn.backend.audit.config.AuditIgnore;

@MappedSuperclass
public abstract class ReferenceEntity extends AbstractEntity {
    private String name;
    @AuditIgnore
    private Boolean archived = false;

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
