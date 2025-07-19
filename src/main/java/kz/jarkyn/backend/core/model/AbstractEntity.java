package kz.jarkyn.backend.core.model;

import jakarta.persistence.*;
import kz.jarkyn.backend.audit.config.IgnoreAudit;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class AbstractEntity implements Persistable<UUID> {

    @Id
    @IgnoreAudit
    private UUID id;
    @IgnoreAudit
    private Instant createdAt;
    @IgnoreAudit
    private Instant lastModifiedAt;

    @PrePersist
    private void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        createdAt = Instant.now();
        lastModifiedAt = Instant.now();
    }

    @PreUpdate
    private void preUpdate() {
        lastModifiedAt = Instant.now();
    }

    @Override
    @Transient
    public boolean isNew() {
        return getId() == null;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(Instant lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        AbstractEntity that = (AbstractEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(id=" + getId() + ")";
    }

}
