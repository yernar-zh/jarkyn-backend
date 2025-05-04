package kz.jarkyn.backend.global.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.AbstractEntity;

import java.util.UUID;

@Entity
@Table(name = "image")
public class ImageEntity extends AbstractEntity {
    private UUID originalFileId;
    private UUID mediumFileId;
    private UUID thumbnailFileId;

    public UUID getOriginalFileId() {
        return originalFileId;
    }

    public void setOriginalFileId(UUID originalFileId) {
        this.originalFileId = originalFileId;
    }

    public UUID getMediumFileId() {
        return mediumFileId;
    }

    public void setMediumFileId(UUID mediumFileId) {
        this.mediumFileId = mediumFileId;
    }

    public UUID getThumbnailFileId() {
        return thumbnailFileId;
    }

    public void setThumbnailFileId(UUID thumbnailFileId) {
        this.thumbnailFileId = thumbnailFileId;
    }
}
