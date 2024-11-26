package kz.jarkyn.backend.core.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import kz.jarkyn.backend.core.model.dto.ImmutablePageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.convert.ConversionService;

import java.util.*;

public class CriteriaSearch<T> {
    private final EntityManager em;
    private final CriteriaBuilder cb;
    private final Class<T> javaClass;
    private final ConversionService conversionService;
    private final CriteriaSearchHolder<T> cs;

    private Specification<T> specification;
    private QueryParams queryParams;

    public CriteriaSearch(EntityManager em, Class<T> javaClass, ConversionService conversionService) {
        this.em = em;
        this.cb = em.getCriteriaBuilder();
        this.javaClass = javaClass;
        this.conversionService = conversionService;
        this.cs = new CriteriaSearchHolder<>(javaClass);
    }

    public void specification(Specification<T> specification) {
        this.specification = specification;
    }

    public void queryParams(QueryParams queryParams) {
        this.queryParams = queryParams;
    }

    public PageResponse<T> getResult() {
        CriteriaQuery<T> query = cb.createQuery(javaClass);
        specification.update(cs, query, cb);
        buildQuery(query, queryParams);

        List<Order> orders = queryParams.getSorts().stream()
                .filter(filter -> cs.attributes.containsKey(filter.getName()))
                .map(filter -> {
                    Expression<?> expression = cs.attributes.get(filter.getName());
                    return switch (filter.getType()) {
                        case ASC -> cb.asc(expression);
                        case DESC -> cb.desc(expression);
                    };
                }).toList();
        query.orderBy(orders);
        query.select(cb.construct(javaClass, cs.attributes.values().toArray(Selection<?>[]::new)));
        TypedQuery<T> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult(queryParams.getPageFirst());
        typedQuery.setMaxResults(queryParams.getPageSize());
        List<T> rows = typedQuery.getResultList();

        CriteriaQuery<Integer> countQuery = cb.createQuery(Integer.class);
        specification.update(cs, countQuery, cb);
        buildQuery(countQuery, queryParams);

        countQuery.select(cb.countDistinct(cs.idAttribute).as(Integer.class));
        Integer count = em.createQuery(countQuery).getResultList().stream().findFirst().orElse(0);
        return ImmutablePageResponse.of(rows, queryParams.getPageFirst(), queryParams.getPageSize(), count);
    }

    private void buildQuery(CriteriaQuery<?> query, QueryParams queryParams) {
        List<Predicate> wherePredicates = new ArrayList<>();
        if (Strings.isNotBlank(queryParams.getSearch())) {
            Expression<?>[] expressions = new Expression<?>[cs.searchAttributes.size() + 1];
            expressions[0] = cb.literal(queryParams.getSearch());
            for (int i = 1; i <= cs.searchAttributes.size(); i++) {
                expressions[i] = cs.searchAttributes.get(i - 1);
            }
            Predicate searchPredicate = cb.isTrue(cb.function("search", Boolean.class, expressions));
            wherePredicates.add(searchPredicate);
        }
        List<Predicate> filterPredicates = getFilterPredicate(queryParams
                .getFilters().stream().filter(filter -> cs.groupByFiledNames.contains(filter.getName())).toList());
        wherePredicates.addAll(filterPredicates);
        query.where(cb.and(wherePredicates.toArray(new Predicate[0])));

        List<Predicate> havingPredicates = getFilterPredicate(queryParams
                .getFilters().stream().filter(filter -> !cs.groupByFiledNames.contains(filter.getName())).toList());
        query.having(cb.and(havingPredicates.toArray(new Predicate[0])));

        query.groupBy(cs.groupByAttributes);
    }

    private List<Predicate> getFilterPredicate(List<QueryParams.Filter> filters) {
        return filters.stream()
                .filter(filter -> cs.attributes.containsKey(filter.getName()))
                .map(filter -> {
                    Expression<Comparable> expression = (Expression<Comparable>) cs.attributes.get(filter.getName());
                    Comparable value = conversionService
                            .convert(filter.getValue(), expression.getJavaType());
                    return switch (filter.getType()) {
                        case EQUAL_TO -> cb.equal(expression, value);
                        case LESS_THEN -> cb.lessThanOrEqualTo(expression, value);
                        case GREATER_THEN -> cb.greaterThanOrEqualTo(expression, value);
                    };
                }).toList();
    }

    public interface Specification<T> {
        void update(CriteriaSearchHolder<T> cs, CriteriaQuery<?> query, CriteriaBuilder cb);
    }
}
