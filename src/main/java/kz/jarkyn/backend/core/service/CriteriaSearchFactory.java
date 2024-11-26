package kz.jarkyn.backend.core.service;

import kz.jarkyn.backend.core.PageSpecification;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CriteriaSearchFactory {
    private final ConversionService conversionService;

    public CriteriaSearchFactory(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public <T> PageSpecification<T> createSpecification(Class<T> javaClass, QueryParams queryParams) {
        return new PageSpecification<>(conversionService, queryParams,
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction(),
                Sort.unsorted());
    }
}
