package kz.jarkyn.backend.core.search;

import jakarta.persistence.EntityManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class SearchFactory {
    private final EntityManager entityManager;

    public SearchFactory(
            EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <R, S, E> CriteriaSearch<R, E> createCriteriaSearch(
            Class<R> responseClass, List<String> searchFields,
            Class<E> entityClass, CriteriaAttributes<E> criteriaAttributes) {
        return new CriteriaSearch<>(entityManager,
                responseClass, entityClass, criteriaAttributes.getAttributes(), searchFields);

    }

    public <R> ListSearch<R> createListSearch(
            Class<R> javaClass, List<String> searchFields,
            Supplier<List<R>> responseSupplier) {
        return new ListSearch<>(javaClass, searchFields, responseSupplier.get());
    }
}
