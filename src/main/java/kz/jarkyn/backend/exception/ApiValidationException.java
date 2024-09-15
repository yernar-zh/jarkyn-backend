package kz.jarkyn.backend.exception;

public class ApiValidationException extends ValidationException {
    public ApiValidationException(String message) {
        super(message);
    }
}