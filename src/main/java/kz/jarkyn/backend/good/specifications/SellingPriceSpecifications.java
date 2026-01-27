package kz.jarkyn.backend.good.specifications;

import kz.jarkyn.backend.good.model.*;
import org.springframework.data.jpa.domain.Specification;

public class SellingPriceSpecifications {
    public static Specification<SellingPriceEntity> good(GoodEntity good) {
        return (root, query, cb) -> cb.equal(root.get(SellingPriceEntity_.good), good);
    }
}