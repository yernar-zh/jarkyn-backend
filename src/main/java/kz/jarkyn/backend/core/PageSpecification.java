package kz.jarkyn.backend.core;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class PageSpecification<T> {
    private final ConversionService conversionService;
    private final QueryParams queryParams;
    private final Specification<T> specification;
    private final Sort orderSort;

    public PageSpecification(
            ConversionService conversionService,
            QueryParams queryParams,
            Specification<T> specification,
            Sort orderSort) {
        this.conversionService = conversionService;
        this.queryParams = queryParams;
        this.specification = specification;
        this.orderSort = orderSort;
    }


    public <V extends Comparable<? super V>> PageSpecification<T> filterAndSort(
            String filedName, SingularAttribute<? super T, V> attribute) {
        Specification<T> specification = this.specification;
        QueryParams.Filter filter = queryParams.getFilters().get(filedName);
        if (filter != null) {
            V value = conversionService.convert(filter.getValue(), attribute.getJavaType());
            Specification<T> newSpecification = switch (filter.getType()) {
                case EQUAL_TO -> filterByEqual(attribute, value);
                case LESS_THEN -> filterByLessThan(attribute, value);
                case GREATER_THEN -> filterByGreaterThan(attribute, value);
            };
            specification = specification.and(newSpecification);
        }

        Sort orderSort = this.orderSort;
        QueryParams.Sort sort = queryParams.getSorts().get(filedName);
        if (sort != null) {
            String property = attribute.getName();
            Sort newSortOrder = switch (sort.getType()) {
                case ASC -> Sort.by(Sort.Order.asc(property));
                case DESC -> Sort.by(Sort.Order.desc(property));
            };
            orderSort = orderSort.and(newSortOrder);
        }
        return new PageSpecification<>(conversionService, queryParams, specification, orderSort);
    }

    public <V extends Comparable<? super V>> Specification<T> filter(
            String filedName, SingularAttribute<? super T, V> attribute) {
        QueryParams.Filter filter = queryParams.getFilters().get(filedName);
        V value = conversionService.convert(filter.getValue(), attribute.getJavaType());
        return switch (filter.getType()) {
            case EQUAL_TO -> filterByEqual(attribute, value);
            case LESS_THEN -> filterByLessThan(attribute, value);
            case GREATER_THEN -> filterByGreaterThan(attribute, value);
        };
    }

    private <V extends Comparable<? super V>> Specification<T> filterByEqual(
            SingularAttribute<? super T, V> attribute, V value) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (value == null) {
                return null;
            }
            return cb.equal(root.get(attribute), value);
        };
    }

    private <V extends Comparable<? super V>> Specification<T> filterByLessThan(
            SingularAttribute<? super T, V> attribute, V value) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (value == null) {
                return null;
            }
            return cb.lessThanOrEqualTo(root.get(attribute), value);
        };
    }

    private <V extends Comparable<? super V>> Specification<T> filterByGreaterThan(
            SingularAttribute<? super T, V> attribute, V value) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (value == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get(attribute), value);
        };
    }

//    private <J, V> Specification<T> filterByEqual(
//            SingularAttribute<T, J> joinAttribute, SingularAttribute<J, V> attribute, V value) {
//        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
//            if (value == null) {
//                return null;
//            }
//            return cb.equal(root.join(joinAttribute, JoinType.LEFT).get(attribute), value);
//        };
//    }

    public Specification<T> getSpecification() {
        return specification;
    }

    public Pageable getPageable() {
        return PageRequest.of(queryParams.getPageNumber(), queryParams.getPageSize(), orderSort);
    }
}