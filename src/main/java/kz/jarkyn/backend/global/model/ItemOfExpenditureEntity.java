package kz.jarkyn.backend.global.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.ReferenceEntity;

@Entity
@Table(name = "item_of_expenditure")
public class ItemOfExpenditureEntity extends ReferenceEntity {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
