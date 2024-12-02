package kz.jarkyn.backend.core.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.counterparty.model.dto.CustomerListResponse;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Map<String, Expression<?>> expressions = new HashMap<>();
        for (Map.Entry<String, CriteriaAttribute<E>> entry : attributes.entrySet()) {
            expressions.put(entry.getKey(), entry.getValue().get(root, query, cb));
        }
        List<Selection<?>> selections = expressions.entrySet().stream()
                .map(entry -> entry.getValue().alias(entry.getKey()))
                .collect(Collectors.toList());
        query.multiselect(selections);
        List<Predicate> wherePredicates = getWherePredicates(expressions, queryParams.getFilters(), cb);
        if (!wherePredicates.isEmpty()) {
            query.where(wherePredicates.toArray(new Predicate[0]));
        }
        List<Tuple> tupleList = em.createQuery(query)
                .setFirstResult(queryParams.getPageFirst()).setMaxResults(queryParams.getPageSize())
                .getResultList();

        List<R> results = tupleList.stream()
                .map(tuple -> createProxy(tuple, expressions.keySet(), responseClass))
                .toList();


        return null;
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
            Map<String, Expression<?>> expressions, List<QueryParams.Filter> filters, CriteriaBuilder cb) {
        return filters.stream().map(filter -> {
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
