package kz.jarkyn.backend.core.exception;

import kz.jarkyn.backend.core.model.AbstractEntity;

import java.util.function.Supplier;

public class ExceptionUtils {
    public static final String ENTITY_NOT_FOUND = "ENTITY_NOT_FOUND";

    public static Supplier<DataValidationException> entityNotFound() {
        return () -> new DataValidationException(ENTITY_NOT_FOUND, "entity not found");
    }

    public static <T> void requireEqualsApi(T first, T second, String fieldName) {
        if (first == null) {
            if (second != null) {
                throw new ApiValidationException(fieldName + " should be same");
            }
        } else if (!first.equals(second)) {
            throw new ApiValidationException(fieldName + " should be same, " + first + " != " + second);
        }
    }

    public static <T extends AbstractEntity> T requireNonNullApi(T entity, String entityName) {
        if (entity == null) {
            throw new ApiValidationException("Resource `" + entityName + "` not found");
        }
        return entity;
    }

    public static void throwRelationDeleteException() {
        throw new DataValidationException("Невозможно удалить объект, так как он используется в других ресурсах.");
    }

    public static void throwCommitedDeleteException() {
        throw new DataValidationException("Невозможно удалить проведённый документ. Сначала отмените проведение.");
    }
}