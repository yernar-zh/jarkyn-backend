package kz.jarkyn.backend.model.product;


import kz.jarkyn.backend.model.common.entity.AbstractEntity;
import kz.jarkyn.backend.model.common.entity.ImageEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class ProductEntity extends AbstractEntity {
    private String name;
    private GroupEntity group;
    private ImageEntity image;
    private Integer maxDiscount;
}
