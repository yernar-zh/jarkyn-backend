package kz.jarkyn.backend.operation.specifications;

import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.operation.mode.TurnoverEntity;
import kz.jarkyn.backend.operation.mode.TurnoverEntity_;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.good.model.WarehouseEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class TurnoverSpecifications {

    public static Specification<TurnoverEntity> warehouseAndGoodEquals(
            WarehouseEntity warehouse, GoodEntity good
    ) {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get(TurnoverEntity_.warehouse), warehouse),
                cb.equal(root.get(TurnoverEntity_.good), good)
        );
    }

    public static Specification<TurnoverEntity> document(DocumentEntity document) {
        return (root, query, cb) -> cb.equal(root.get(TurnoverEntity_.document), document);
    }


    public static Specification<TurnoverEntity> momentLessThan(Instant moment) {
        return (root, query, cb) -> cb.lessThan(root.get(TurnoverEntity_.moment), moment);
    }

    public static Specification<TurnoverEntity> momentGreaterThanEqual(Instant moment) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(TurnoverEntity_.moment), moment);
    }

    public static Specification<TurnoverEntity> isIncome() {
        return (root, query, cb) -> cb.greaterThan(root.get(TurnoverEntity_.quantity), 0);
    }

    public static Specification<TurnoverEntity> isOutflow() {
        return (root, query, cb) -> cb.lessThan(root.get(TurnoverEntity_.quantity), 0);
    }
}