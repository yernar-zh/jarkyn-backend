package kz.jarkyn.backend.exception;

import kz.jarkyn.backend.model.common.AbstractEntity;

import java.util.function.Supplier;

public class ExceptionUtils {
    public static final String NOT_FOUND = "NOT_FOUND";

    public static Supplier<DataValidationException> notFound(String entity, Long id) {
        String message = "Resource `" + entity + "` with id: `" + id + "` not found";
        return () -> new DataValidationException(NOT_FOUND, message);
    }

    public static <T> void requireEqualsApi(T first, T second, String message) {
        if (!first.equals(second)) {
            throw new ApiValidationException(message);
        }
    }

    public static <T extends AbstractEntity> T requireNonNullApi(T entity, String entityName) {
        if (entity == null) {
            throw new ApiValidationException("Resource `" + entityName + "` not found");
        }
        return entity;
    }
}