
package kz.jarkyn.backend.model.good;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.model.common.AbstractEntity;

@Entity
@Table(name = "transport")
public class TransportEntity extends AbstractEntity {
    private String name;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private TransportEntity parent;
    private Integer position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransportEntity getParent() {
        return parent;
    }

    public void setParent(TransportEntity parent) {
        this.parent = parent;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
