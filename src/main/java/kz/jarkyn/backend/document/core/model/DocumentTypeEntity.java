package kz.jarkyn.backend.document.core.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.EnumTypeEntity;

@Entity
@Table(name = "document_type")
public class DocumentTypeEntity extends EnumTypeEntity {
}
