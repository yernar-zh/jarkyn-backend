package kz.jarkyn.backend.core.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import java.util.*;

public class CriteriaAttributes<E> {
    private final Map<String, CriteriaAttribute<E>> attributes;

    private CriteriaAttributes(Map<String, CriteriaAttribute<E>> attributes
    ) {
        this.attributes = attributes;
    }

    public Map<String, CriteriaAttribute<E>> getAttributes() {
        return attributes;
    }


    public static <E> Builder<E> builder() {
        return new Builder<>();
    }

    public static class Builder<E> {
        private final Map<String, CriteriaAttribute<E>> attributes;

        public Builder() {
            this.attributes = new LinkedHashMap<>();
        }

        public Builder<E> add(String fieldName, CriteriaAttribute<E> attribute) {
            attributes.put(fieldName, attribute);
            return this;
        }

        public Builder<E> add(String fieldName, ShortCriteriaAttribute<E> attribute) {
            attributes.put(fieldName, (root, query, cb, expressions) -> attribute.get(root));
            return this;
        }

        public CriteriaAttributes<E> build() {
            return new CriteriaAttributes<>(attributes);
        }
    }

    public interface CriteriaAttribute<E> {
        Expression<?> get(Root<E> root, CriteriaQuery<?> query,
                          CriteriaBuilder cb, Map<String, Expression<?>> map);
    }

    public interface ShortCriteriaAttribute<E> {
        Expression<?> get(Root<E> root);
    }
}
