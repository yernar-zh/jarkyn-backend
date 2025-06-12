package kz.jarkyn.backend.core.model;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class EnumTypeEntity extends ReferenceEntity {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
