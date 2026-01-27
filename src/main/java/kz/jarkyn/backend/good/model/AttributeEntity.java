
package kz.jarkyn.backend.good.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.ReferenceEntity;

@Entity
@Table(name = "attribute")
public class AttributeEntity extends ReferenceEntity {
    @ManyToOne
    @JoinColumn(name = "group_id")
    private AttributeGroupEntity group;
    @Column(name = "search_keywords")
    private String searchKeywords;

    public AttributeGroupEntity getGroup() {
        return group;
    }

    public void setGroup(AttributeGroupEntity group) {
        this.group = group;
    }

    public String getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }
}
