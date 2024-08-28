package kz.jarkyn.backend.model.document.entity;


import kz.jarkyn.backend.model.common.entity.AbstractEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "document")
public class DocumentEntity extends AbstractEntity {
    private String name;
    private LocalDateTime moment;
    private String comment;
}
