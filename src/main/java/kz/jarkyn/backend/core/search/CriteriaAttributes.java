package kz.jarkyn.backend.core.search;

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
            this.attributes = new HashMap<>();
        }

        public Builder<E> add(String fieldName, CriteriaAttribute<E> attribute) {
            attributes.put(fieldName, attribute);
            return this;
        }


        public CriteriaAttributes<E> build() {
            return new CriteriaAttributes<>(attributes);
        }
    }
}
