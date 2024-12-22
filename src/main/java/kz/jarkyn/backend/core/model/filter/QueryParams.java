package kz.jarkyn.backend.core.model.filter;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.util.Pair;

import java.util.*;

public class QueryParams {
    private static final List<Pair<String, Filter.Type>> FILTER_SUFFIX = new ArrayList<>() {{
        add(Pair.of("[max]", Filter.Type.LESS_THEN));
        add(Pair.of("[min]", Filter.Type.GREATER_THEN));
        add(Pair.of("[exists]", Filter.Type.EXISTS));
        add(Pair.of("", Filter.Type.EQUAL_TO));
    }};
    private static final List<Pair<String, Sort.Type>> SORT_PREFIX = new ArrayList<>() {{
        add(Pair.of("-", Sort.Type.DESC));
        add(Pair.of("", Sort.Type.ASC));
    }};
    private static final String SORT_FIELD = "sort";
    private static final String SORT_SPLITTER = ",";
    private static final String SEARCH_FIELD = "search";
    private static final String PAGE_FIRST_FIELD = "page.first";
    private static final String PAGE_SIZE_FIELD = "page.size";
    private static final Set<String> STATIC_FIELDS = Set.of(
            SORT_FIELD, SORT_SPLITTER, SEARCH_FIELD, PAGE_FIRST_FIELD, PAGE_SIZE_FIELD);

    private final String search;
    private final Integer pageFirst;
    private final Integer pageSize;
    private final List<Filter> filters;
    private final List<Sort> sorts;


    public static QueryParams of(Map<String, String> allParams) {
        return new QueryParams(allParams);
    }

    public QueryParams(Map<String, String> allParams) {
        this.search = allParams.get(SEARCH_FIELD);
        this.pageFirst = Integer.valueOf(allParams.getOrDefault(PAGE_FIRST_FIELD, "0"));
        this.pageSize = Integer.valueOf(allParams.getOrDefault(PAGE_SIZE_FIELD, "20"));
        this.filters = allParams.entrySet().stream()
                .filter(entry -> !STATIC_FIELDS.contains(entry.getKey()))
                .map(entry -> {
                    List<String> values = Arrays.stream(entry.getValue().split(SORT_SPLITTER)).toList();
                    for (Pair<String, Filter.Type> suffix : FILTER_SUFFIX) {
                        if (entry.getKey().endsWith(suffix.getFirst())) {
                            String name = entry.getKey()
                                    .substring(0, entry.getKey().length() - suffix.getFirst().length());
                            return new Filter(name, values, suffix.getSecond());
                        }
                    }
                    throw new RuntimeException("Invalid filter suffix: " + entry.getKey());
                }).toList();
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
                }).toList();
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

        public enum Type {EQUAL_TO, GREATER_THEN, LESS_THEN, EXISTS}
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
