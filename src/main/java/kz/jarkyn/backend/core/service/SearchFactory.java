package kz.jarkyn.backend.core.service;

import jakarta.persistence.EntityManager;
import kz.jarkyn.backend.core.PageSpecification;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class SearchFactory {
    private final ConversionService conversionService;
    private final EntityManager entityManager;

    public SearchFactory(
            ConversionService conversionService,
            EntityManager entityManager) {
        this.conversionService = conversionService;
        this.entityManager = entityManager;
    }

    public <T> PageSpecification<T> createSpecification(Class<T> javaClass, QueryParams queryParams) {
        return new PageSpecification<>(conversionService, queryParams,
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction(),
                Sort.unsorted());
    }

    public <R, E> CriteriaSearch<R, E> createCriteriaSearch(
            Class<R> responseClass, List<String> searchFields,
            Class<E> entityClass, CriteriaAttributes<E> criteriaAttributes) {
        return new CriteriaSearch<>(entityManager,
                responseClass, entityClass, criteriaAttributes.getAttributes(),searchFields);

    }

    @Cacheable(value = "response_cache", key = "#javaClass.name")
    public <R> ListSearch<R> createListSearch(
            Class<R> javaClass, List<String> searchFields,
            Supplier<List<R>> responseSupplier) {
        return new ListSearch<>(javaClass, searchFields, responseSupplier.get());

    }
}
