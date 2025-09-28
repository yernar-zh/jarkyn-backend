package kz.jarkyn.backend.document.bind.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import org.immutables.value.internal.$processor$.meta.$GsonMirrors;

import java.math.BigDecimal;

@Entity
@Table(name = "bind_document")
public class BindDocumentEntity extends AbstractEntity {
    @$GsonMirrors.Ignore
    @ManyToOne
    @JoinColumn(name = "primary_document_id")
    private DocumentEntity primaryDocument;

    @ManyToOne
    @JoinColumn(name = "related_document_id")
    private DocumentEntity relatedDocument;

    private BigDecimal amount;

    private Integer position;

    public DocumentEntity getPrimaryDocument() {
        return primaryDocument;
    }

    public void setPrimaryDocument(DocumentEntity primaryDocument) {
        this.primaryDocument = primaryDocument;
    }

    public DocumentEntity getRelatedDocument() {
        return relatedDocument;
    }

    public void setRelatedDocument(DocumentEntity relatedDocument) {
        this.relatedDocument = relatedDocument;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
