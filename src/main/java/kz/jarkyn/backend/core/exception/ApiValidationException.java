package kz.jarkyn.backend.core.exception;

public class ApiValidationException extends ValidationException {
    public ApiValidationException(String message) {
        super(message);
    }
}