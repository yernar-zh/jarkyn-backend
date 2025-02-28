package kz.jarkyn.backend.warehouse.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.ReferenceEntity;

@Entity
@Table(name = "warehouse")
public class WarehouseEntity extends ReferenceEntity {
}
