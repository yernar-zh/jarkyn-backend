
package kz.jarkyn.backend.good.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.ReferenceEntity;

@Entity
@Table(name = "groups")
public class GroupEntity extends ReferenceEntity {
    @Column(name = "search_keywords")
    private String searchKeywords;
    @Column(name = "minimum_markup")
    private Integer minimumMarkup;
    @Column(name = "selling_markup")
    private Integer sellingMarkup;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private GroupEntity parent;
    private Integer position;

    public String getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public Integer getMinimumMarkup() {
        return minimumMarkup;
    }

    public void setMinimumMarkup(Integer minimumMarkup) {
        this.minimumMarkup = minimumMarkup;
    }

    public Integer getSellingMarkup() {
        return sellingMarkup;
    }

    public void setSellingMarkup(Integer sellingMarkup) {
        this.sellingMarkup = sellingMarkup;
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
