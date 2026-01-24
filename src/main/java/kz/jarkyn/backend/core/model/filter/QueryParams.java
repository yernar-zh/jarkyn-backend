package kz.jarkyn.backend.core.model.filter;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class QueryParams {
    public static List<String> NAME_SEARCH = List.of("name");
    public static final String IN_SEPARATOR = "<%sep%>";
    private static final List<Pair<String, Filter.Type>> FILTER_SUFFIX = new ArrayList<>() {{
        add(Pair.of("[lte]", Filter.Type.LESS_OR_EQ));
        add(Pair.of("[gte]", Filter.Type.GREATER_OR_EQ));
        add(Pair.of("[lt]", Filter.Type.LESS));
        add(Pair.of("[gt]", Filter.Type.GREATER));
        add(Pair.of("[like]", Filter.Type.CONTAINS));
        add(Pair.of("[notLike]", Filter.Type.NOT_CONTAINS));
        add(Pair.of("[not]", Filter.Type.NOT_EQUAL_TO));
        add(Pair.of("", Filter.Type.EQUAL_TO));
    }};
    private static final List<Pair<String, Sort.Type>> SORT_PREFIX = new ArrayList<>() {{
        add(Pair.of("-", Sort.Type.DESC));
        add(Pair.of("", Sort.Type.ASC));
    }};
    private static final String SORT_FIELD = "sort";
    private static final String SEARCH_FIELD = "search";
    private static final String PAGE_FIRST_FIELD = "page.first";
    private static final String PAGE_SIZE_FIELD = "page.size";
    private static final Set<String> STATIC_FIELDS = Set.of(SORT_FIELD, SEARCH_FIELD, PAGE_FIRST_FIELD, PAGE_SIZE_FIELD);

    private final String search;
    private final Integer pageFirst;
    private final Integer pageSize;
    private final List<Filter> filters;
    private final List<Sort> sorts;

    public static QueryParams ofMulty(Map<String, List<String>> allParams) {
        return new QueryParams(allParams);
    }

    public static QueryParams of(Map<String, String> allParams) {
        Map<String, List<String>> converted = allParams.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(List.of(e.getValue()))));
        return new QueryParams(converted);
    }

    public static QueryParams of() {
        return new QueryParams(Map.of());
    }

    private QueryParams(Map<String, List<String>> allParams) {
        this.search = getFirst(allParams, SEARCH_FIELD, null);
        this.pageFirst = Integer.valueOf(getFirst(allParams, PAGE_FIRST_FIELD, "0"));
        this.pageSize = Integer.valueOf(getFirst(allParams, PAGE_SIZE_FIELD, "20"));
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
                }).toList();
        this.sorts = allParams.getOrDefault(SORT_FIELD, List.of())
                .stream().filter(Strings::isNotBlank)
                .map(paramName -> {
                    for (Pair<String, Sort.Type> prefix : SORT_PREFIX) {
                        if (paramName.startsWith(prefix.getFirst())) {
                            String name = paramName.substring(prefix.getFirst().length());
                            return new Sort(name, prefix.getSecond());
                        }
                    }
                    throw new RuntimeException("Invalid filter suffix: " + paramName);
                }).toList();
    }

    private String getFirst(Map<String, List<String>> allParams, String key, String defaultValue) {
        return allParams.getOrDefault(key, List.of()).stream().findFirst().orElse(defaultValue);
    }

    public String getSearch() {
        return search;
    }

    public Integer getPageFirst() {
        return pageFirst;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public static class Filter {
        private final String name;
        private final List<String> values;
        private final Type type;

        public Filter(String name, List<String> values, Type type) {
            this.name = name;
            this.values = values;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public List<String> getValues() {
            return values;
        }

        public Type getType() {
            return type;
        }

        public enum Type {
            EQUAL_TO, NOT_EQUAL_TO,
            GREATER_OR_EQ, LESS_OR_EQ,
            GREATER, LESS,
            CONTAINS, NOT_CONTAINS
        }
    }

    public static class Sort {
        public static Sort NAME_ASC = new Sort("name", Type.ASC);
        public static Sort MOMENT_DESC = new Sort("moment", Sort.Type.DESC);

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
