package kz.jarkyn.backend.core.search;

import jakarta.persistence.criteria.*;
import kz.jarkyn.backend.core.model.EnumTypeEntity;
import kz.jarkyn.backend.core.model.ReferenceEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CriteriaFilters<E> {
    private final List<CriteriaFilter<E>> filters;

    private CriteriaFilters(List<CriteriaFilter<E>> filters
    ) {
        this.filters = filters;
    }

    public List<CriteriaFilter<E>> getFilters() {
        return filters;
    }


    public static <E> Builder<E> builder() {
        return new Builder<>();
    }

    public static class Builder<E> {
        private final List<CriteriaFilter<E>> filters;

        public Builder() {
            this.filters = new ArrayList<>();
        }

        public Builder<E> add(CriteriaFilter<E> filter) {
            filters.add(filter);
            return this;
        }

        public CriteriaFilters<E> build() {
            return new CriteriaFilters<>(filters);
        }
    }

    public interface CriteriaFilter<E> {
        Predicate get(Root<E> root, CriteriaQuery<?> query,
                      CriteriaBuilder cb, Map<String, Expression<?>> map);
    }
}
