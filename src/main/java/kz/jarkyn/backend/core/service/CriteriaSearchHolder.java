package kz.jarkyn.backend.core.service;

import jakarta.persistence.criteria.*;
import org.springframework.data.util.Pair;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public class CriteriaSearchHolder<T> {
    private final Class<T> javaClass;
    protected Map<String, Expression<?>> attributes;
    protected Expression<?> idAttribute;
    protected List<Expression<?>> searchAttributes;
    protected Set<String> groupByFiledNames;
    protected List<Expression<?>> groupByAttributes;
    protected Predicate staticPredicate;

    public CriteriaSearchHolder(Class<T> javaClass) {
        this.javaClass = javaClass;
    }

    public AttributeBuilder attributes() {
        return new AttributeBuilder();
    }

    public void idAttribute(String filedName) {
        if (this.attributes == null) {
            throw new IllegalArgumentException("first set attributes");
        }
        idAttribute = attributes.get(filedName);

    }

    public void searchAttributes(String... filedNames) {
        if (this.attributes == null) {
            throw new IllegalArgumentException("first set attributes");
        }
        searchAttributes = Arrays.stream(filedNames)
                .map(filedName -> attributes.get(filedName))
                .collect(Collectors.toList());

    }

    public void groupByAttributes(String... filedNames) {
        if (this.attributes == null) {
            throw new IllegalArgumentException("first set attributes");
        }
        groupByFiledNames = Arrays.stream(filedNames).collect(Collectors.toSet());
        groupByAttributes = groupByFiledNames.stream()
                .map(filedName -> attributes.get(filedName))
                .collect(Collectors.toList());
    }

    public void where(Predicate predicate) {
        this.staticPredicate = predicate;
    }

    public class AttributeBuilder {
        final private List<Class<?>> attributeTypes;
        final private List<Pair<String, Expression<?>>> attributes;

        private AttributeBuilder() {
            Constructor<?> constructors = CriteriaSearchHolder.this.javaClass.getDeclaredConstructors()[0];
            this.attributeTypes = Arrays.asList(constructors.getParameterTypes());
            this.attributes = new ArrayList<>();
        }

        public AttributeBuilder add(
                String filedName,
                Expression<?> expression) {
            if (!attributeTypes.get(attributes.size()).equals(expression.getJavaType())) {
                throw new IllegalArgumentException(("field type not match. filedName: %s, expected type: %s ," +
                                                    "given type: %s")
                        .formatted(filedName, attributeTypes.get(attributes.size()), expression.getJavaType()));
            }
            attributes.add(Pair.of(filedName, expression));
            return this;
        }

        public void finish() {
            CriteriaSearchHolder.this.attributes = new LinkedHashMap<>();
            for (Pair<String, Expression<?>> pair : attributes) {
                CriteriaSearchHolder.this.attributes.put(pair.getFirst(), pair.getSecond());
            }
        }
    }
}
