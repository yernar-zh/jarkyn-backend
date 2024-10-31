package kz.jarkyn.backend.model.audit;

import jakarta.persistence.*;
import kz.jarkyn.backend.config.RevisionInfoListener;
import org.hibernate.envers.DefaultRevisionEntity;

import java.util.UUID;

@Entity
@org.hibernate.envers.RevisionEntity(RevisionInfoListener.class)
@Table(name = "revision_info")
public class RevisionInfoEntity extends DefaultRevisionEntity {
    @Column(name = "user_id")
    private UUID userId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}