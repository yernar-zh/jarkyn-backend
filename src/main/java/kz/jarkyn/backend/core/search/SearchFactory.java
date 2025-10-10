package kz.jarkyn.backend.core.search;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class SearchFactory {
    private final EntityManager entityManager;

    public SearchFactory(
            EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <R, E> CriteriaSearch<R, E> createCriteriaSearch(
            Class<R> responseClass, List<String> searchFields, QueryParams.Sort defaultSort,
            Class<E> entityClass, CriteriaAttributes<E> criteriaAttributes) {
        return new CriteriaSearch<>(entityManager,
                responseClass, entityClass, criteriaAttributes.getAttributes(), List.of(),
                searchFields, defaultSort);

    }

    public <R, E> CriteriaSearch<R, E> createCriteriaSearch(
            Class<R> responseClass, List<String> searchFields, QueryParams.Sort defaultSort,
            Class<E> entityClass, CriteriaAttributes<E> criteriaAttributes, CriteriaFilters<E> filters) {
        return new CriteriaSearch<>(entityManager,
                responseClass, entityClass, criteriaAttributes.getAttributes(), filters.getFilters(),
                searchFields, defaultSort);

    }

    public <R> ListSearch<R> createListSearch(
            Class<R> javaClass, List<String> searchFields, QueryParams.Sort defaultSort,
            Supplier<List<R>> responseSupplier) {
        return new ListSearch<>(javaClass, searchFields, responseSupplier.get(), defaultSort);
    }
}
