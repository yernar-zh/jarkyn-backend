package kz.jarkyn.backend.core.model.filter;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueryParams {
    private static final List<Pair<String, Filter.Type>> FILTER_SUFFIX = new ArrayList<>() {{
        add(Pair.of("[max]", Filter.Type.LESS_THEN));
        add(Pair.of("[min]", Filter.Type.GREATER_THEN));
        add(Pair.of("", Filter.Type.EQUAL_TO));
    }};
    private static final List<Pair<String, Sort.Type>> SORT_PREFIX = new ArrayList<>() {{
        add(Pair.of("-", Sort.Type.DESC));
        add(Pair.of("", Sort.Type.ASC));
    }};
    private static final String SORT_FIELD = "sort";
    private static final String SORT_SPLITTER = ",";
    private static final String SEARCH_FIELD = "search";
    private static final String PAGE_NUMBER_FIELD = "page.number";
    private static final String PAGE_SIZE_FIELD = "page.size";
    private static final Set<String> STATIC_FIELDS = Set.of(
            SORT_FIELD, SORT_SPLITTER, SEARCH_FIELD, PAGE_NUMBER_FIELD, PAGE_SIZE_FIELD);

    private final String search;
    private final Integer pageNumber;
    private final Integer pageSize;
    private final Map<String, Filter> filters;
    private final Map<String, Sort> sorts;


    public static QueryParams of(Map<String, String> allParams) {
        return new QueryParams(allParams);
    }

    public QueryParams(Map<String, String> allParams) {
        this.search = allParams.get(SEARCH_FIELD);
        this.pageNumber = Integer.valueOf(allParams.getOrDefault(PAGE_NUMBER_FIELD, "0"));
        this.pageSize = Integer.valueOf(allParams.getOrDefault(PAGE_SIZE_FIELD, "20"));
        this.filters = allParams.entrySet().stream()
                .filter(entry -> !STATIC_FIELDS.contains(entry.getKey()))
                .map(entry -> {
                    for (Pair<String, Filter.Type> suffix : FILTER_SUFFIX) {
                        if (entry.getKey().endsWith(suffix.getFirst())) {
                            String name = entry.getKey()
                                    .substring(0, entry.getKey().length() - suffix.getFirst().length());
                            return new Filter(name, entry.getValue(), suffix.getSecond());
                        }
                    }
                    throw new RuntimeException("Invalid filter suffix: " + entry.getKey());
                }).collect(Collectors.toMap(Filter::getName, Function.identity()));
        this.sorts = Arrays.stream(allParams.getOrDefault(SORT_FIELD, "").split(SORT_SPLITTER))
                .filter(Strings::isNotBlank)
                .map(paramName -> {
                    for (Pair<String, Sort.Type> prefix : SORT_PREFIX) {
                        if (paramName.startsWith(prefix.getFirst())) {
                            String name = paramName.substring(prefix.getFirst().length());
                            return new Sort(name, prefix.getSecond());
                        }
                    }
                    throw new RuntimeException("Invalid filter suffix: " + paramName);
                }).collect(Collectors.toMap(Sort::getName, Function.identity()));
    }

    public String getSearch() {
        return search;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Map<String, Filter> getFilters() {
        return filters;
    }

    public Map<String, Sort> getSorts() {
        return sorts;
    }

    public static class Filter {
        private final String name;
        private final String value;
        private final Type type;

        public Filter(String name, String value, Type type) {
            this.name = name;
            this.value = value;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public Type getType() {
            return type;
        }

        public enum Type {EQUAL_TO, GREATER_THEN, LESS_THEN}
    }

    public static class Sort {
        private final String name;
        private final Type type;

        public Sort(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return type;
        }

        public enum Type {ASC, DESC}
    }
}
