package kz.jarkyn.backend.document.sale.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

@Entity
@Table(name = "sale")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class SaleEntity extends DocumentEntity {
}
