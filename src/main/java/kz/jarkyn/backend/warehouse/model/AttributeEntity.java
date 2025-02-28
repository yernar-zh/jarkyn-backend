
package kz.jarkyn.backend.warehouse.model;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.ReferenceEntity;

@Entity
@Table(name = "attribute")
public class AttributeEntity extends ReferenceEntity {
    @ManyToOne
    @JoinColumn(name = "group_id")
    private AttributeGroupEntity group;
    private Integer position;

    public AttributeGroupEntity getGroup() {
        return group;
    }

    public void setGroup(AttributeGroupEntity group) {
        this.group = group;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
