package kz.jarkyn.backend.core.search;

import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        } else if (javaClass == LocalDate.class) {
            return str -> LocalDate.parse(str, DateTimeFormatter.ISO_DATE);
        } else if (javaClass == LocalDateTime.class) {
            return str -> LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME);
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

    public static Number getZero(Class<?> javaClass) {
        if (javaClass == Integer.class) {
            return 0;
        } else if (javaClass == Long.class) {
            return 0L;
        } else if (javaClass == BigInteger.class) {
            return BigInteger.ZERO;
        } else if (javaClass == BigDecimal.class) {
            return BigDecimal.ZERO;
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
                if (SearchUtils.getConvertor(method.getReturnType()) == null) {
                    return createProxy(fieldName + ".", map, method.getReturnType());
                }
                Object value = map.get(prefix + fieldName);
                if (value == null) {
                    return SearchUtils.getZero(method.getReturnType());
                }
                return value;
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
