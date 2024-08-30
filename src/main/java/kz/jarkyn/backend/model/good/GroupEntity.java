
package kz.jarkyn.backend.model.good;


import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kz.jarkyn.backend.model.common.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "groups")
public class GroupEntity extends AbstractEntity {
    private String name;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private GroupEntity parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroupEntity getParent() {
        return parent;
    }

    public void setParent(GroupEntity parent) {
        this.parent = parent;
    }
}
