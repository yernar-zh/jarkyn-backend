
package kz.jarkyn.backend.warehouse.model;


import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.ReferenceEntity;

@Entity
@Table(name = "groups")
public class GroupEntity extends ReferenceEntity {
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private GroupEntity parent;
    private Integer position;

    public GroupEntity getParent() {
        return parent;
    }

    public void setParent(GroupEntity parent) {
        this.parent = parent;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
