
package kz.jarkyn.backend.model.user;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.model.common.AbstractEntity;

@Entity
@Table(name = "role")
public class RoleEntity extends AbstractEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
