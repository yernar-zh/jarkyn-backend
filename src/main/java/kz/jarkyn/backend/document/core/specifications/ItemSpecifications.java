package kz.jarkyn.backend.document.core.specifications;

import kz.jarkyn.backend.document.core.model.*;
import kz.jarkyn.backend.good.model.GoodEntity;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecifications {
    public static Specification<ItemEntity> document(DocumentEntity document) {
        return (root, query, cb) -> cb.equal(root.get(ItemEntity_.document), document);
    }

    public static Specification<ItemEntity> good(GoodEntity good) {
        return (root, query, cb) -> cb.equal(root.get(ItemEntity_.good), good);
    }
}