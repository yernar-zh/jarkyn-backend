package kz.jarkyn.backend.counterparty.repository;

import kz.jarkyn.backend.core.model.dto.ImmutablePage;
import kz.jarkyn.backend.core.model.dto.ImmutablePageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.utils.PrefixSearch;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.convert.ConversionService;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

public class SearchList<R> {
    private final ConversionService conversionService;
    private final List<Row> rows;

    public SearchList(
            ConversionService conversionService,
            List<R> list, Class<R> javaClass, String... searchFields) {
        this.conversionService = conversionService;
        rows = list.stream().map(data -> {
            Map<String, Field<?>> fields = new HashMap<>();
            for (Method method : javaClass.getMethods()) {
                Field<?> field = buildFiled(data, method);
                fields.put(field.getName(), field);
            }
            String[] texts = Arrays.stream(searchFields)
                    .map(searchField -> fields.get(searchField).getValue())
                    .map(Object::toString).toArray(String[]::new);
            return new Row(data, new PrefixSearch(texts), fields);
        }).toList();
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
                Field<?> field = row.getFields().get(filter.getName());
                if (field == null) {
                    continue;
                }
                if (!filter(field, filter)) {
                    return false;
                }
            }
            return true;
        };
    }

    private <V extends Comparable<V>> boolean filter(Field<V> field, QueryParams.Filter filter) {
        V value = conversionService.convert(filter.getValue(), field.getJavaClass());
        Objects.requireNonNull(value);
        return switch (filter.getType()) {
            case EQUAL_TO -> field.getValue().equals(value);
            case LESS_THEN -> field.getValue().compareTo(value) <= 0;
            case GREATER_THEN -> field.getValue().compareTo(value) >= 0;
        };
    }

    private Comparator<Row> sort(QueryParams queryParams) {
        Comparator<Row> mainComparator = Comparator.comparing(_ -> true);
        for (QueryParams.Sort sort : queryParams.getSorts()) {
            Comparator<Row> comparator = Comparator.comparing(row -> {
                Field<?> field = row.getFields().get(sort.getName());
                if (field == null) {
                    return null;
                }
                return field.getValue();
            });
            switch (sort.getType()) {
                case ASC -> mainComparator = mainComparator.thenComparing(comparator);
                case DESC -> mainComparator = mainComparator.thenComparing(comparator.reversed());
            }
        }
        return mainComparator;
    }

    private <V extends Comparable<V>> Field<V> buildFiled(R data, Method method) {
        String name = Introspector.decapitalize(method.getName().substring(3));
        Class<V> javaClass = (Class<V>) method.getReturnType();
        V value;
        method.setAccessible(true);
        try {
            value = (V) method.invoke(data);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return new Field<>(name, value, javaClass);
    }

    private class Row {
        private final R data;
        private final PrefixSearch search;
        private final Map<String, Field<?>> fields;

        private Row(R data, PrefixSearch search, Map<String, Field<?>> fields) {
            this.data = data;
            this.search = search;
            this.fields = fields;
        }

        public R getData() {
            return data;
        }

        public PrefixSearch getSearch() {
            return search;
        }

        public Map<String, Field<?>> getFields() {
            return fields;
        }
    }

    private class Field<V extends Comparable<V>> {
        private final String name;
        private final V value;
        private final Class<V> javaClass;


        private Field(String name, V value, Class<V> javaClass) {
            this.name = name;
            this.value = value;
            this.javaClass = javaClass;
        }

        public String getName() {
            return name;
        }

        public V getValue() {
            return value;
        }

        public Class<V> getJavaClass() {
            return javaClass;
        }
    }
}
