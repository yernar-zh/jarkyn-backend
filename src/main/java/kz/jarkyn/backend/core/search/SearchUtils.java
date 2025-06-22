package kz.jarkyn.backend.core.search;

import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

class SearchUtils {
    public static Function<String, Object> getConvertor(Class<?> javaClass) {
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
        } else if (javaClass == Instant.class) {
            return Instant::parse;
        } else if (javaClass == OffsetTime.class) {
            return str -> OffsetTime.parse(str, DateTimeFormatter.ISO_DATE);
        } else if (javaClass == OffsetDateTime.class) {
            return str -> OffsetDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME);
        } else if (javaClass == BigInteger.class) {
            return BigInteger::new;
        } else if (javaClass == BigDecimal.class) {
            return BigDecimal::new;
        } else if (javaClass == UUID.class) {
            return UUID::fromString;
        } else if (javaClass.isEnum()) {
            return str -> Enum.valueOf((Class<? extends Enum>) javaClass, str);
        } else {
            return null;
        }
    }

    public static BiFunction<Number, Number, Number> getSum(Class<?> javaClass) {
        if (javaClass == Integer.class) {
            return (a, b) -> a.intValue() + b.intValue();
        } else if (javaClass == Long.class) {
            return (a, b) -> a.longValue() + b.longValue();
        } else if (javaClass == BigInteger.class) {
            return (a, b) -> ((BigInteger) a).add((BigInteger) b);
        } else if (javaClass == BigDecimal.class) {
            return (a, b) -> ((BigDecimal) a).add((BigDecimal) b);
        } else {
            return null;
        }
    }

    public static Object createProxy(String prefix, Map<String, ?> map, Class<?> resultClass) {
        InvocationHandler handler = (proxy, method, args) -> {
            String methodName = method.getName();
            if (methodName.startsWith("get") && methodName.length() > 3) {
                String fieldName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                if (SearchUtils.getConvertor(method.getReturnType()) == null &&
                    !Collection.class.isAssignableFrom(method.getReturnType())) {
                    return createProxy(fieldName + ".", map, method.getReturnType());
                }
                return map.get(prefix + fieldName);
            }
            throw new UnsupportedOperationException("Method not supported: " + methodName);
        };
        return Proxy.newProxyInstance(
                resultClass.getClassLoader(),
                new Class<?>[]{resultClass},
                handler
        );
    }

}
