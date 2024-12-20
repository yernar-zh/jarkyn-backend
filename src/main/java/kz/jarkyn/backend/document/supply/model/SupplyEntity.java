package kz.jarkyn.backend.document.supply.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

@Entity
@Table(name = "supply")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class SupplyEntity extends DocumentEntity {
}
