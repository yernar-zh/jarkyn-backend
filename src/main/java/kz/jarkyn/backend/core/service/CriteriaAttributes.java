package kz.jarkyn.backend.core.service;

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


    public static <E> Builder<E> builder(Class<E> javaClass) {
        return new Builder<>(javaClass);
    }

    public static class Builder<E> {
        private final Map<String, CriteriaAttribute<E>> attributes;

        public Builder(Class<E> javaClass) {
            this.attributes = new HashMap<>();
        }

        public Builder<E> add(String id, CriteriaAttribute<E> attribute) {
            attributes.put(id, attribute);
            return this;
        }


        public CriteriaAttributes<E> build() {
            return new CriteriaAttributes<>(attributes);
        }
    }
}
