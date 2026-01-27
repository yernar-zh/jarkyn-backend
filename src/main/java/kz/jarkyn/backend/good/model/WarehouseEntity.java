package kz.jarkyn.backend.good.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.ReferenceEntity;

@Entity
@Table(name = "warehouse")
public class WarehouseEntity extends ReferenceEntity {
}
