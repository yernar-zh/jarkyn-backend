package kz.jarkyn.backend.core.search;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
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
}
