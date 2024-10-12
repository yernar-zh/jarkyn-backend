package kz.jarkyn.backend.exception;

import kz.jarkyn.backend.model.common.AbstractEntity;

import java.util.function.Supplier;

public class ExceptionUtils {
    public static final String ENTITY_NOT_FOUND = "ENTITY_NOT_FOUND";

    public static Supplier<DataValidationException> entityNotFound() {
        return () -> new DataValidationException(ENTITY_NOT_FOUND, "entity not found");
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