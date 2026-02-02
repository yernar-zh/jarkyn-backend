package kz.jarkyn.backend.core.search;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.model.dto.ImmutablePage;
import kz.jarkyn.backend.core.model.dto.ImmutablePageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.utils.PrefixSearch;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CriteriaSearch<R, E> implements Search<R> {
    private final EntityManager em;
    private final Class<R> responseClass;
    private final Class<E> entityClass;
    private final Map<String, CriteriaAttributes.CriteriaAttribute<E>> attributes;
    private final List<CriteriaFilters.CriteriaFilter<E>> filters;
    private final List<String> searchByAttributeNames;
    private final QueryParams.Sort defaultSort;

    public CriteriaSearch(
            EntityManager entityManager,
            Class<R> responseClass,
            Class<E> entityClass,
            Map<String, CriteriaAttributes.CriteriaAttribute<E>> attributes,
            List<CriteriaFilters.CriteriaFilter<E>> filters,
            List<String> searchByAttributeNames,
            QueryParams.Sort defaultSort
    ) {
        this.em = entityManager;
        this.responseClass = responseClass;
        this.entityClass = entityClass;
        this.attributes = attributes;
        this.filters = filters;
        this.searchByAttributeNames = searchByAttributeNames;
        this.defaultSort = defaultSort;
    }

    @Override
    public PageResponse<R> getResult(QueryParams queryParams) {
        List<R> row = getRows(queryParams);
        Pair<Integer, Map<String, Object>> sum = getSum(queryParams);
        return ImmutablePageResponse.of(row, sum.getSecond(),
                ImmutablePage.of(queryParams.getPageFirst(), queryParams.getPageSize(), sum.getFirst()));
    }

    private List<R> getRows(QueryParams queryParams) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<E> root = query.from(entityClass);
        Map<String, Expression<?>> expressions = new HashMap<>();
        for (Map.Entry<String, CriteriaAttributes.CriteriaAttribute<E>> entry : attributes.entrySet()) {
            Expression<?> expression = entry.getValue().get(root, query, cb, expressions);
            expressions.put(entry.getKey(), expression);
        }
        List<Selection<?>> selections = expressions.entrySet().stream()
                .map(entry -> entry.getValue().alias(entry.getKey()))
                .collect(Collectors.toList());
        query.multiselect(selections);
        List<Predicate> wherePredicates = getWherePredicates(expressions, queryParams, root, query, cb, expressions);
        if (!wherePredicates.isEmpty()) {
            query.where(wherePredicates.toArray(new Predicate[0]));
        }
        List<Order> orders = getOrders(expressions, queryParams, cb);
        if (!orders.isEmpty()) {
            query.orderBy(orders.toArray(new Order[0]));
        }
        return em.createQuery(query)
                .setFirstResult(queryParams.getPageFirst()).setMaxResults(queryParams.getPageSize())
                .getResultList().stream()
                .map(tuple -> tupleToClass(tuple, responseClass))
                .toList();

    }

    private R tupleToClass(Tuple tuple, Class<R> responseClass) {
        Map<String, Object> map = tuple.getElements().stream()
                .collect(HashMap::new, (m, v) -> m.put(v.getAlias(), tuple.get(v.getAlias())), HashMap::putAll);
        return (R) SearchUtils.createProxy("", map, responseClass);
    }

    private Pair<Integer, Map<String, Object>> getSum(QueryParams queryParams) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<E> root = query.from(entityClass);
        Map<String, Expression<?>> expressions = new HashMap<>();
        for (Map.Entry<String, CriteriaAttributes.CriteriaAttribute<E>> entry : attributes.entrySet()) {
            Expression<?> expression = entry.getValue().get(root, query, cb, expressions);
            expressions.put(entry.getKey(), expression);
        }
        Stream<Selection<?>> selections = expressions.entrySet().stream()
                .map(entry -> {
                    if (!Number.class.isAssignableFrom(entry.getValue().getJavaType())) {
                        return cb.nullLiteral(entry.getValue().getJavaType()).alias(entry.getKey());
                    }
                    return cb.sum((Expression<Number>) entry.getValue()).alias(entry.getKey());
                });
        query.multiselect(Stream.concat(Stream.of(cb.count(cb.conjunction()).alias("totalCount")), selections).toList());
        List<Predicate> wherePredicates = getWherePredicates(expressions, queryParams, root, query, cb, expressions);
        if (!wherePredicates.isEmpty()) {
            query.where(wherePredicates.toArray(new Predicate[0]));
        }
        Tuple tuple = em.createQuery(query).getSingleResult();

        int count = tuple.get("totalCount", Long.class).intValue();
        Map<String, Object> map = tuple.getElements().stream()
                .filter(element -> Objects.nonNull(tuple.get(element.getAlias())))
                .collect(HashMap::new,
                        (m, v) -> m.put(v.getAlias(), tuple.get(v.getAlias())),
                        HashMap::putAll);
        return Pair.of(count, map);

    }


    private List<Predicate> getWherePredicates(
            Map<String, Expression<?>> expressions, QueryParams queryParams,
            Root<E> root, CriteriaQuery<Tuple> query, CriteriaBuilder cb, Map<String, Expression<?>> map) {
        List<Predicate> filterPredicates = queryParams.getFilters().stream().map(filter -> {
            Expression<?> expression = expressions.get(filter.getName());
            if (expression == null) {
                return cb.conjunction();
            }
            return filter.getValues().stream().distinct()
                    .map(Objects.requireNonNull(SearchUtils.getConvertor(expression.getJavaType())))
                    .map(filterValue -> switch (filter.getType()) {
                        case EQUAL_TO -> cb.equal(expression, filterValue);
                        case NOT_EQUAL_TO -> cb.notEqual(expression, filterValue);
                        case LESS_OR_EQ ->
                                cb.lessThanOrEqualTo((Expression<Comparable>) expression, (Comparable) filterValue);
                        case GREATER_OR_EQ ->
                                cb.greaterThanOrEqualTo((Expression<Comparable>) expression, (Comparable) filterValue);
                        case LESS -> cb.lessThan((Expression<Comparable>) expression, (Comparable) filterValue);
                        case GREATER -> cb.greaterThan((Expression<Comparable>) expression, (Comparable) filterValue);
                        case CONTAINS -> cb.like((Expression<String>) expression, "%" + filterValue + "%");
                        case NOT_CONTAINS -> cb.notLike((Expression<String>) expression, "%" + filterValue + "%");
                    }).reduce((predicate1, predicate2) -> switch (filter.getType()) {
                        case NOT_EQUAL_TO, NOT_CONTAINS -> cb.and(predicate1, predicate2);
                        default -> cb.or(predicate1, predicate2);
                    }).orElse(cb.conjunction());
        }).toList();
        List<Expression<String>> searchFields = searchByAttributeNames.stream().map(expressions::get)
                .filter(Objects::nonNull)
                .map(expression -> (Expression<String>) expression)
                .toList();
        List<Predicate> searchPredicates = Stream.of(queryParams.getSearch())
                .filter(Objects::nonNull).map(PrefixSearch::split)
                .flatMap(Collection::stream)
                .map(pattern -> "%" + pattern.toLowerCase() + "%")
                .map(pattern -> searchFields.stream()
                        .map(expression -> cb.like(cb.lower(expression), pattern))
                        .reduce(cb::or).orElse(cb.conjunction()))
                .toList();
        List<Predicate> queryPredicates = filters.stream().map(filter -> filter.get(root, query, cb, expressions)).toList();

        return Stream.of(filterPredicates.stream(), searchPredicates.stream(), queryPredicates.stream())
                .flatMap(Function.identity()).toList();
    }

    private List<Order> getOrders(
            Map<String, Expression<?>> expressions, QueryParams queryParams, CriteriaBuilder cb) {
        return Stream.of(queryParams.getSorts().stream(), Stream.of(defaultSort))
                .flatMap(Function.identity())
                .map(sort -> {
                    Expression<?> expression = expressions.get(sort.getName());
                    return switch (sort.getType()) {
                        case ASC -> cb.asc(expression);
                        case DESC -> cb.desc(expression);
                    };
                }).toList();
    }
}
