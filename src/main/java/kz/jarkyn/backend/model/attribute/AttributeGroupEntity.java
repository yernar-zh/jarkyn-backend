
package kz.jarkyn.backend.model.attribute;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.model.common.AbstractEntity;

@Entity
@Table(name = "attribute_group")
public class AttributeGroupEntity extends AbstractEntity {
    private String name;
    private Integer position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

}
