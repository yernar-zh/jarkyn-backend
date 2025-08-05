package kz.jarkyn.backend.global.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.EnumTypeEntity;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "coverage")
public class CoverageEntity extends EnumTypeEntity {
    public static String FULL = "FULL";
    public static String PARTIAL = "PARTIAL";
    public static String NONE = "NONE";
}
