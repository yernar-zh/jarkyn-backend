package kz.jarkyn.backend.core;

import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class CoreSpecifications {

    public static <T, V> Specification<T> filterByEqualTo(SingularAttribute<T, V> attribute, V value) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (value == null) {
                return null;
            }
            return cb.equal(root.get(attribute), value);
        };
    }

    public static <T, J, V> Specification<T> filterByEqualTo(
            SingularAttribute<T, J> joinAttribute, SingularAttribute<J, V> attribute, V value) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (value == null) {
                return null;
            }
            return cb.equal(root.join(joinAttribute, JoinType.LEFT).get(attribute), value);
        };
    }
}