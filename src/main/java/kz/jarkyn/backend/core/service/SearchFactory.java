package kz.jarkyn.backend.core.service;

import kz.jarkyn.backend.core.PageSpecification;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.counterparty.repository.ListSearch;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class SearchFactory {
    private final ConversionService conversionService;

    public SearchFactory(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public <T> PageSpecification<T> createSpecification(Class<T> javaClass, QueryParams queryParams) {
        return new PageSpecification<>(conversionService, queryParams,
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction(),
                Sort.unsorted());
    }

    @Cacheable(value = "response_cache", key = "#javaClass.name")
    public <R> ListSearch<R> createListSearch(
            Class<R> javaClass, List<String> searchFields,
            Supplier<List<R>> responseSupplier) {
        return new ListSearch<>(javaClass, responseSupplier.get(), searchFields);

    }
}
