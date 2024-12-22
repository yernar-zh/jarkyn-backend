package kz.jarkyn.backend.core.search;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.model.dto.ImmutablePage;
import kz.jarkyn.backend.core.model.dto.ImmutablePageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CriteriaSearch<R, E> implements Search<R> {
    private final EntityManager em;
    private final Class<R> responseClass;
    private final Class<E> entityClass;
    private final Map<String, CriteriaAttribute<E>> attributes;
    private final List<String> searchByAttributeNames;

    public CriteriaSearch(EntityManager entityManager, Class<R> responseClass, Class<E> entityClass, Map<String, CriteriaAttribute<E>> attributes, List<String> searchByAttributeNames) {
        this.em = entityManager;
        this.responseClass = responseClass;
        this.entityClass = entityClass;
        this.attributes = attributes;
        this.searchByAttributeNames = searchByAttributeNames;
    }

    @Override
    public PageResponse<R> getResult(QueryParams queryParams) {
        List<R> row = getRows(queryParams);
        Pair<Integer, R> sum = getSum(queryParams);
        return ImmutablePageResponse.of(row, sum.getSecond(),
                ImmutablePage.of(queryParams.getPageFirst(), queryParams.getPageSize(), sum.getFirst()));
    }

    private List<R> getRows(QueryParams queryParams) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<E> root = query.from(entityClass);
        Map<String, Expression<?>> expressions = attributes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(root, query, cb)));
        List<Selection<?>> selections = expressions.entrySet().stream()
                .map(entry -> entry.getValue().alias(entry.getKey()))
                .collect(Collectors.toList());
        query.multiselect(selections);
        List<Predicate> wherePredicates = getWherePredicates(expressions, queryParams, cb);
        if (!wherePredicates.isEmpty()) {
            query.where(wherePredicates.toArray(new Predicate[0]));
        }
        List<Order> orders = getOrders(expressions, queryParams, cb);
        if (!orders.isEmpty()) {
            query.orderBy(orders.toArray(new Order[0]));
        }
        List<Tuple> tupleList = em.createQuery(query)
                .setFirstResult(queryParams.getPageFirst()).setMaxResults(queryParams.getPageSize())
                .getResultList();

        return tupleList.stream()
                .map(tuple -> (R) createProxy("", tuple, responseClass))
                .toList();

    }

    private Pair<Integer, R> getSum(QueryParams queryParams) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<E> root = query.from(entityClass);
        Map<String, Expression<?>> expressions = attributes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(root, query, cb)));
        Stream<Selection<?>> selections = expressions.entrySet().stream()
                .map(entry -> {
                    if (!Number.class.isAssignableFrom(entry.getValue().getJavaType())) {
                        return cb.nullLiteral(entry.getValue().getJavaType()).alias(entry.getKey());
                    }
                    return cb.sum((Expression<Number>) entry.getValue()).alias(entry.getKey());
                });
        query.multiselect(Stream.concat(Stream.of(cb.count(cb.conjunction()).alias("totalCount")), selections).toList());
        List<Predicate> wherePredicates = getWherePredicates(expressions, queryParams, cb);
        if (!wherePredicates.isEmpty()) {
            query.where(wherePredicates.toArray(new Predicate[0]));
        }
        Tuple tuple = em.createQuery(query)
                .setFirstResult(queryParams.getPageFirst()).setMaxResults(queryParams.getPageSize())
                .getSingleResult();

        int count = tuple.get("totalCount", Long.class).intValue();
        R result = (R) createProxy("", tuple, responseClass);
        return Pair.of(count, result);

    }

    private Object createProxy(String prefix, Tuple tuple, Class<?> resultClass) {
        InvocationHandler handler = (proxy, method, args) -> {
            String methodName = method.getName();
            if (methodName.startsWith("get") && methodName.length() > 3) {
                String fieldName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                if (SearchUtils.getConvertor(method.getReturnType()) == null) {
                    return createProxy(fieldName + ".", tuple, method.getReturnType());
                }
                return tuple.get(prefix + fieldName);
            }
            throw new UnsupportedOperationException("Method not supported: " + methodName);
        };
        return Proxy.newProxyInstance(
                resultClass.getClassLoader(),
                new Class<?>[]{resultClass},
                handler
        );
    }

    private List<Predicate> getWherePredicates(
            Map<String, Expression<?>> expressions, QueryParams queryParams, CriteriaBuilder cb) {
        List<Predicate> filterPredicates = queryParams.getFilters().stream().map(filter -> {
            Expression<?> expression = expressions.get(filter.getName());
            if (expression == null) {
                return cb.conjunction();
            }
            if (filter.getType().equals(QueryParams.Filter.Type.EXISTS)) {
                String value = filter.getValues().getFirst().trim().toLowerCase();
                if (value.equals("true")) {
                    return cb.isNotNull(expression);
                } else if (value.equals("false")) {
                    return cb.isNull(expression);
                } else {
                    throw new ApiValidationException("[exist] can be only true or false");
                }
            }
            return filter.getValues().stream().distinct()
                    .map(Objects.requireNonNull(SearchUtils.getConvertor(expression.getJavaType())))
                    .map(filterValue -> switch (filter.getType()) {
                        case EQUAL_TO -> cb.equal(expression, filterValue);
                        case LESS_THEN -> cb.lessThanOrEqualTo((Expression<Comparable>) expression,
                                (Comparable) filterValue);
                        case GREATER_THEN -> cb.greaterThanOrEqualTo((Expression<Comparable>) expression,
                                (Comparable) filterValue);
                        case EXISTS -> throw new IllegalStateException();
                    }).reduce(cb::or).orElse(cb.conjunction());
        }).toList();
        List<Expression<?>> searchFields = searchByAttributeNames.stream().map(expressions::get)
                .filter(Objects::nonNull).collect(Collectors.toList());
        List<Predicate> searchPredicates = Stream.of(queryParams.getSearch())
                .filter(Objects::nonNull).map(cb::literal).map(pattern -> {
                    List<Expression<?>> searchArgument = Stream.concat(Stream.of(pattern),
                            searchFields.stream()).toList();
                    return cb.isTrue(cb.function("search", Boolean.class,
                            searchArgument.toArray(new Expression[0])));
                }).toList();

        return Stream.concat(filterPredicates.stream(), searchPredicates.stream()).toList();
    }

    private List<Order> getOrders(
            Map<String, Expression<?>> expressions, QueryParams queryParams, CriteriaBuilder cb) {
        return queryParams.getSorts().stream().map(sort -> {
            Expression<?> expression = expressions.get(sort.getName());
            return switch (sort.getType()) {
                case ASC -> cb.asc(expression);
                case DESC -> cb.desc(expression);
            };
        }).toList();
    }
}
