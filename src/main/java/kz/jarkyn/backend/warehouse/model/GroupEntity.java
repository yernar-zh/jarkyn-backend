
package kz.jarkyn.backend.warehouse.model;


import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kz.jarkyn.backend.audit.config.IgnoreAudit;
import kz.jarkyn.backend.core.model.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "groups")
public class GroupEntity extends AbstractEntity {
    private String name;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private GroupEntity parent;
    @IgnoreAudit
    private Integer position;

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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
