
package kz.jarkyn.backend.model.product;


import kz.jarkyn.backend.model.common.entity.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "groups")
public class GroupEntity extends AbstractEntity {
    private String name;
}
