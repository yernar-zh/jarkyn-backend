package kz.jarkyn.backend.counterparty.repository;

import kz.jarkyn.backend.core.model.dto.ImmutablePage;
import kz.jarkyn.backend.core.model.dto.ImmutablePageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.utils.PrefixSearch;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.util.Pair;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchList<R> {
    private final List<Row> rows;

    public SearchList(List<R> list, String... searchFields) {
        rows = list.stream().map(data -> {
            Map<String, Set<Object>> fields = getFields(data).stream()
                    .collect(Collectors.groupingBy(
                            Pair::getFirst,
                            Collectors.mapping(Pair::getSecond, Collectors.toSet())
                    ));
            String[] texts = Arrays.stream(searchFields)
                    .map(fields::get).filter(Objects::nonNull).map(Object::toString).toArray(String[]::new);
            return new Row(data, new PrefixSearch(texts), fields);
        }).toList();
    }

    private List<Pair<String, Object>> getFields(Object object) {
        if (object == null) {
            return Collections.emptyList();
        }
        Class<?> javaClass = object.getClass();
        Function<String, Object> convertor = getConvertor(javaClass);
        if (convertor != null) {
            return List.of(Pair.of("", object));
        }
        if (object instanceof Collection) {
            return ((Collection<?>) object).stream().map(this::getFields).flatMap(List::stream).toList();
        }
        List<Pair<String, Object>> result = new ArrayList<>();
        for (Method method : javaClass.getMethods()) {
            if (!method.getName().startsWith("get") || method.getName().equals("getClass")) {
                continue;
            }
            String name = Introspector.decapitalize(method.getName().substring(3));
            method.setAccessible(true);
            Object value;
            try {
                value = method.invoke(object);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            for (Pair<String, Object> field : getFields(value)) {
                String childName = name + (Strings.isNotBlank(field.getFirst()) ? "." + field.getFirst() : "");
                result.add(Pair.of(childName, field.getSecond()));
            }
        }
        return result;
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

    public PageResponse<R> getResponse(QueryParams queryParams) {
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
                Function<String, Object> convertor = getConvertor(firstRowValue.getClass());
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
