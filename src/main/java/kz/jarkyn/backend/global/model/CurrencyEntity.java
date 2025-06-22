package kz.jarkyn.backend.global.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.EnumTypeEntity;
import kz.jarkyn.backend.core.model.ReferenceEntity;

@Entity
@Table(name = "currency")
public class CurrencyEntity extends EnumTypeEntity {
}
