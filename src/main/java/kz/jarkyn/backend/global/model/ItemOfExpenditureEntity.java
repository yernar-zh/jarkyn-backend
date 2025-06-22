package kz.jarkyn.backend.global.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.EnumTypeEntity;

@Entity
@Table(name = "item_of_expenditure")
public class ItemOfExpenditureEntity extends EnumTypeEntity {
}
