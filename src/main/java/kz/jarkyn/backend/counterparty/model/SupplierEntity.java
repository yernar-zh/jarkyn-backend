package kz.jarkyn.backend.counterparty.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;

@Entity
@Table(name = "supplier")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class SupplierEntity extends CounterpartyEntity {
}