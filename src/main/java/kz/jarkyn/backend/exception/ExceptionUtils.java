package kz.jarkyn.backend.exception;

import java.util.function.Supplier;

public class ExceptionUtils {
    public static final String NOT_FOUND = "NOT_FOUND";

    public static Supplier<DataValidationException> notFound(String entity, Long id) {
        String message = "Resource `" + entity + "` with id: `" + id + "` not found";
        return () -> new DataValidationException(NOT_FOUND, message);
    }
}