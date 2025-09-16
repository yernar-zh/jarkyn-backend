package kz.jarkyn.backend.warehouse.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.audit.config.AuditIgnore;
import kz.jarkyn.backend.core.model.ReferenceEntity;
import kz.jarkyn.backend.global.model.ImageEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "good")
public class GoodEntity extends ReferenceEntity {
    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;
    @ManyToOne
    @JoinColumn(name = "image_id")
    @AuditIgnore
    private ImageEntity image;
    private BigDecimal weight;
    @Column(name = "minimum_price")
    private Integer minimumPrice;
    @Column(name = "search_keywords")
    private String searchKeywords;

    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    public ImageEntity getImage() {
        return image;
    }

    public void setImage(ImageEntity image) {
        this.image = image;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(Integer minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public String getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }
}
