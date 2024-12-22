package kz.jarkyn.backend.core.search;

import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.model.dto.ImmutablePage;
import kz.jarkyn.backend.core.model.dto.ImmutablePageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.utils.PrefixSearch;
import org.apache.logging.log4j.util.Strings;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListSearch<R> implements Search<R> {
    private final List<Row> rows;

    public ListSearch(Class<R> javaClass, List<String> searchFields, List<R> list) {
        rows = list.stream().map(data -> {
            Map<String, Object> fields = getRowValues(javaClass, data);
            String[] searchTexts = searchFields.stream()
                    .map(fields::get).filter(Objects::nonNull)
                    .map(Object::toString).toArray(String[]::new);
            return new Row(data, new PrefixSearch(searchTexts), fields);
        }).toList();
    }

    private Map<String, Object> getRowValues(Class<?> valueClass, Object value) {
        if (value == null) {
            return Collections.emptyMap();
        }
        Function<String, Object> convertor = SearchUtils.getConvertor(valueClass);
        if (convertor != null) {
            return Map.of("", value);
        }
        if (Collection.class.isAssignableFrom(valueClass)) {
            throw new IllegalArgumentException("Collection type not supported");
        }
        Map<String, Object> result = new HashMap<>();
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
            Map<String, Object> subValues = getRowValues(method.getReturnType(), methodReturnValue);
            for (Map.Entry<String, Object> entry : subValues.entrySet()) {
                String childName = name + (Strings.isNotBlank(entry.getKey()) ? "." + entry.getValue() : "");
                result.put(childName, entry.getValue());
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
            return queryParams.getFilters().stream().map(filter -> {
                if (!row.getValues().containsKey(filter.getName())) {
                    return true;
                }
                Object rowValue = row.getValues().get(filter.getName());
                if (filter.getType().equals(QueryParams.Filter.Type.EXISTS)) {
                    String value = filter.getValues().getFirst().trim().toLowerCase();
                    if (value.equals("true")) {
                        return rowValue != null;
                    } else if (value.equals("false")) {
                        return rowValue == null;
                    } else {
                        throw new ApiValidationException("[exist] can be only true or false");
                    }
                }
                if (rowValue == null) {
                    return false;
                }
                return filter.getValues().stream().distinct()
                        .map(Objects.requireNonNull(SearchUtils.getConvertor(rowValue.getClass())))
                        .map(filterValue -> switch (filter.getType()) {
                            case EQUAL_TO -> rowValue.equals(filterValue);
                            case LESS_THEN -> ((Comparable) rowValue).compareTo(filterValue) <= 0;
                            case GREATER_THEN -> ((Comparable) rowValue).compareTo(filterValue) >= 0;
                            case EXISTS -> throw new IllegalStateException();
                        }).reduce((b1, b2) -> b1 || b2).orElse(Boolean.TRUE);
            }).reduce((b1, b2) -> b1 && b2).orElse(Boolean.TRUE);
        };
    }

    private Comparator<Row> sort(QueryParams queryParams) {
        return queryParams.getSorts().stream().map(sort -> {
            Comparator<Row> comparator = Comparator.comparing(row -> {
                Object rowValue = row.getValues().get(sort.getName());
                if (rowValue == null) {
                    return null;
                }
                return (Comparable) rowValue;
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
        private final Map<String, Object> values;

        public Row(R data, PrefixSearch search, Map<String, Object> values) {
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

        public Map<String, Object> getValues() {
            return values;
        }
    }
}
