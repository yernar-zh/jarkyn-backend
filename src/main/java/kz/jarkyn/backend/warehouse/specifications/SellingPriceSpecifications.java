package kz.jarkyn.backend.warehouse.specifications;

import kz.jarkyn.backend.warehouse.model.*;
import org.springframework.data.jpa.domain.Specification;

public class SellingPriceSpecifications {
    public static Specification<SellingPriceEntity> good(GoodEntity good) {
        return (root, query, cb) -> cb.equal(root.get(SellingPriceEntity_.good), good);
    }
}