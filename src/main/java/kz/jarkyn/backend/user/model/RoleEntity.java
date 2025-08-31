
package kz.jarkyn.backend.user.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.EnumTypeEntity;


@Entity
@Table(name = "role")
public class RoleEntity extends EnumTypeEntity {
}
