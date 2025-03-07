package kz.jarkyn.backend.warehouse.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.global.model.ImageEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "good")
public class GoodEntity extends AbstractEntity {
    private String name;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;
    @ManyToOne
    @JoinColumn(name = "image_id")
    private ImageEntity image;
    private BigDecimal weight;
    @Column(name = "minimum_price")
    private Integer minimumPrice;
    private Boolean archived;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }
}
