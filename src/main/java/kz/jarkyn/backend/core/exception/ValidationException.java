package kz.jarkyn.backend.core.exception;

public abstract class ValidationException extends RuntimeException {
    ValidationException(String message) {
        super(message);
    }
}