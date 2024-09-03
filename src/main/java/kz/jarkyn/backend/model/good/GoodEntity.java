package kz.jarkyn.backend.model.good;


import jakarta.persistence.*;
import kz.jarkyn.backend.model.common.AbstractEntity;
import kz.jarkyn.backend.model.common.ImageEntity;

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
    @Column(name = "purchase_price")
    private Integer purchasePrice;
    @Column(name = "minimum_price")
    private Integer minimumPrice;

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

    public Integer getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Integer purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Integer getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(Integer minimumPrice) {
        this.minimumPrice = minimumPrice;
    }
}
