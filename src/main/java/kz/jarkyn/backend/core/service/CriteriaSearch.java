package kz.jarkyn.backend.core.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import kz.jarkyn.backend.core.model.dto.ImmutablePage;
import kz.jarkyn.backend.core.model.dto.ImmutablePageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.counterparty.model.dto.CustomerListResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CriteriaSearch<R, E> {
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

    public PageResponse<R> getResult(QueryParams queryParams) {
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

        List<R> results = tupleList.stream()
                .map(tuple -> createProxy(tuple, expressions.keySet(), responseClass))
                .toList();

        CriteriaQuery<Integer> countQuery = cb.createQuery(Integer.class);
        Root<E> countRoot = countQuery.from(entityClass);
        Map<String, Expression<?>> countExpressions = attributes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(countRoot, countQuery, cb)));
        countQuery.select(cb.count(cb.conjunction()).as(Integer.class));
        List<Predicate> countWherePredicates = getWherePredicates(countExpressions, queryParams, cb);
        if (!countWherePredicates.isEmpty()) {
            countQuery.where(countWherePredicates.toArray(new Predicate[0]));
        }
        Integer count = em.createQuery(countQuery).getSingleResult();
        return ImmutablePageResponse.of(results, ImmutablePage
                .of(queryParams.getPageFirst(), queryParams.getPageSize(), count));
    }

    private R createProxy(Tuple tuple, Set<String> attributeKeys, Class<R> resultClass) {
        InvocationHandler handler = (proxy, method, args) -> {
            String methodName = method.getName();
            if (methodName.startsWith("get") && methodName.length() > 3) {
                String fieldName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                if (attributeKeys.contains(fieldName)) {
                    return tuple.get(fieldName);
                }
            }
            throw new UnsupportedOperationException("Method not supported: " + methodName);
        };
        return (R) Proxy.newProxyInstance(
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
            Function<String, Object> convertor = getConvertor(expression.getJavaType());
            Set<Object> filterValues = filter.getValues().stream()
                    .map(filterValue -> convertor.apply(filterValue)).collect(Collectors.toSet());
            Object firstFilterValues = filterValues.iterator().next();
            return switch (filter.getType()) {
                case EQUAL_TO -> {
                    CriteriaBuilder.In<Object> in = cb.in(expression);
                    filterValues.forEach(in::value);
                    yield in;
                }
                case LESS_THEN -> cb.lessThanOrEqualTo((Expression<Comparable>) expression,
                        (Comparable) firstFilterValues);
                case GREATER_THEN -> cb.greaterThanOrEqualTo((Expression<Comparable>) expression,
                        (Comparable) firstFilterValues);
            };
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

    private Function<String, Object> getConvertor(Class<?> javaClass) {
        if (javaClass == Integer.class) {
            return Integer::valueOf;
        } else if (javaClass == Long.class) {
            return Long::valueOf;
        } else if (javaClass == Double.class) {
            return Double::valueOf;
        } else if (javaClass == Float.class) {
            return Float::valueOf;
        } else if (javaClass == Boolean.class) {
            return Boolean::valueOf;
        } else if (javaClass == String.class) {
            return x -> x;
        } else if (javaClass == LocalDate.class) {
            return str -> LocalDate.parse(str, DateTimeFormatter.ISO_DATE);
        } else if (javaClass == LocalDateTime.class) {
            return str -> LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME);
        } else if (javaClass == BigInteger.class) {
            return BigInteger::new;
        } else if (javaClass == UUID.class) {
            return UUID::fromString;
        } else if (javaClass.isEnum()) {
            return str -> Enum.valueOf((Class<? extends Enum>) javaClass, str);
        } else {
            return null;
        }
    }

}
