package kz.jarkyn.backend.warehouse.specifications;

import kz.jarkyn.backend.warehouse.model.*;
import org.springframework.data.jpa.domain.Specification;

public class GoodAttributeSpecifications {
    public static Specification<GoodAttributeEntity> good(GoodEntity good) {
        return (root, query, cb) -> cb.equal(root.get(GoodAttributeEntity_.good), good);
    }

    public static Specification<GoodAttributeEntity> attribute(AttributeEntity attribute) {
        return (root, query, cb) -> cb.equal(root.get(GoodAttributeEntity_.attribute), attribute);
    }
}