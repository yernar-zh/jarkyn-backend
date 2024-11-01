
package kz.jarkyn.backend.good.model;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.AbstractEntity;

@Entity
@Table(name = "attribute")
public class AttributeEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "group_id")
    private AttributeGroupEntity group;
    private String name;
    private Integer position;

    public AttributeGroupEntity getGroup() {
        return group;
    }

    public void setGroup(AttributeGroupEntity group) {
        this.group = group;
    }

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
