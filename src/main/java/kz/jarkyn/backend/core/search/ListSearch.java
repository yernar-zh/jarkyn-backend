package kz.jarkyn.backend.core.search;

import kz.jarkyn.backend.core.model.dto.ImmutablePage;
import kz.jarkyn.backend.core.model.dto.ImmutablePageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.utils.PrefixSearch;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.util.Pair;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListSearch<R> implements Search<R> {
    private final List<Row> rows;

    public ListSearch(Class<R> javaClass, List<String> searchFields, List<R> list) {
        rows = list.stream().map(data -> {
            Map<String, Set<Object>> fields = getRowValues(javaClass, data).stream()
                    .collect(Collectors.groupingBy(
                            Pair::getFirst,
                            Collectors.mapping(Pair::getSecond, Collectors.toSet())
                    ));
            String[] texts = searchFields.stream()
                    .map(fields::get).filter(Objects::nonNull).flatMap(Set::stream)
                    .map(Object::toString).toArray(String[]::new);
            return new Row(data, new PrefixSearch(texts), fields);
        }).toList();
    }

    private List<Pair<String, Object>> getRowValues(Class<?> valueClass, Object value) {
        if (value == null) {
            return Collections.emptyList();
        }
        Function<String, Object> convertor = SearchUtils.getConvertor(valueClass);
        if (convertor != null) {
            return List.of(Pair.of("", value));
        }
        List<Pair<String, Object>> result = new ArrayList<>();
        for (Method method : valueClass.getMethods()) {
            if (!method.getName().startsWith("get")) {
                throw new RuntimeException(method + " is not a getter");
            }
            String name = Introspector.decapitalize(method.getName().substring(3));
            method.setAccessible(true);
            Object methodReturnValue;
            try {
                methodReturnValue = method.invoke(value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            List<Pair<String, Object>> subValues;
            if (Collection.class.isAssignableFrom(method.getReturnType())) {
                ParameterizedType genericType = (ParameterizedType) method.getGenericReturnType();
                Class<?> itemClass = (Class<?>) genericType.getActualTypeArguments()[0];
                subValues = ((Collection<?>) methodReturnValue).stream()
                        .map(itemValue -> getRowValues(itemClass, itemValue))
                        .flatMap(List::stream).toList();
            } else {
                subValues = getRowValues(method.getReturnType(), methodReturnValue);
            }
            for (Pair<String, Object> subValue : subValues) {
                String childName = name + (Strings.isNotBlank(subValue.getFirst()) ? "." + subValue.getFirst() : "");
                result.add(Pair.of(childName, subValue.getSecond()));
            }
        }
        return result;
    }

    @Override
    public PageResponse<R> getResult(QueryParams queryParams) {
        List<R> result = rows.stream().filter(filter(queryParams)).sorted(sort(queryParams))
                .map(Row::getData).toList();
        int totalCount = result.size();
        int fromIndex = Math.min(totalCount, queryParams.getPageFirst());
        int toIndex = Math.min(totalCount, fromIndex + queryParams.getPageSize());
        List<R> pageResult = result.subList(fromIndex, toIndex);
        return ImmutablePageResponse.of(pageResult, ImmutablePage.of(queryParams.getPageFirst(), queryParams.getPageSize(), totalCount));
    }

    private Predicate<Row> filter(QueryParams queryParams) {
        return row -> {
            if (Strings.isNotBlank(queryParams.getSearch())) {
                if (!row.getSearch().contains(queryParams.getSearch())) {
                    return false;
                }
            }
            for (QueryParams.Filter filter : queryParams.getFilters()) {
                Set<Object> rowValues = row.getValues().get(filter.getName());
                if (rowValues == null) {
                    continue;
                }
                if (rowValues.isEmpty()) {
                    return false;
                }
                Object firstRowValue = rowValues.iterator().next();
                Function<String, Object> convertor = SearchUtils.getConvertor(firstRowValue.getClass());
                Set<Object> filterValues = filter.getValues().stream()
                        .map(filterValue -> convertor.apply(filterValue)).collect(Collectors.toSet());
                Object firstFilterValues = filterValues.iterator().next();
                return switch (filter.getType()) {
                    case EQUAL_TO -> rowValues.containsAll(filterValues);
                    case LESS_THEN -> ((Comparable) firstRowValue).compareTo(firstFilterValues) <= 0;
                    case GREATER_THEN -> ((Comparable) firstRowValue).compareTo(firstFilterValues) >= 0;
                };
            }
            return true;
        };
    }

    private Comparator<Row> sort(QueryParams queryParams) {
        return queryParams.getSorts().stream().map(sort -> {
            Comparator<Row> comparator = Comparator.comparing(row -> {
                Set<Object> rowValues = row.getValues().get(sort.getName());
                if (rowValues == null || rowValues.isEmpty()) {
                    return null;
                }
                return (Comparable) rowValues.iterator().next();
            });
            return switch (sort.getType()) {
                case ASC -> comparator;
                case DESC -> comparator.reversed();
            };
        }).reduce(Comparator::thenComparing).orElse(Comparator.comparing(_ -> true));
    }

    private class Row {
        private final R data;
        private final PrefixSearch search;
        private final Map<String, Set<Object>> values;

        public Row(R data, PrefixSearch search, Map<String, Set<Object>> values) {
            this.data = data;
            this.search = search;
            this.values = values;
        }

        public R getData() {
            return data;
        }

        public PrefixSearch getSearch() {
            return search;
        }

        public Map<String, Set<Object>> getValues() {
            return values;
        }
    }
}
