package kz.jarkyn.backend.document.change.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

//@Entity
//@Table(name = "change")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class ChargeEntity extends DocumentEntity {
}
