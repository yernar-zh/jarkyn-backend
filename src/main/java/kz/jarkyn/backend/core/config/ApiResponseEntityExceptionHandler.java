package kz.jarkyn.backend.core.config;


import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.exception.DataValidationException;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.ExceptionResponse;
import kz.jarkyn.backend.core.model.dto.ImmutableExceptionResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class ApiResponseEntityExceptionHandler {

    protected final Log logger = LogFactory.getLog(getClass());

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleException(
            Exception ex, WebRequest request) {
        switch (ex) {
            case HttpMessageNotReadableException httpMessageNotReadableException -> {
                return handBindException(httpMessageNotReadableException);
            }
            case DataValidationException dataValidationException -> {
                String code = dataValidationException.getCode();
                return dataValidationExceptionResponse(code, dataValidationException.getMessage(), dataValidationException);
            }
            case ApiValidationException apiValidationException -> {
                return apiValidationExceptionResponse(apiValidationException.getMessage(), apiValidationException);
            }
            case null, default -> {
                return handleInternalException(ex);
            }
        }
    }

    public ResponseEntity<ExceptionResponse> handBindException(HttpMessageNotReadableException ex) {
        return apiValidationExceptionResponse("json error", ex);
    }


    private ResponseEntity<ExceptionResponse> handleInternalException(Exception ex) {
        String code = "Internal Server Error";
        String message = "Something went wrong";

        logger.error(message, ex);

        return exceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, code, message, ex);
    }

    private ResponseEntity<ExceptionResponse> dataValidationExceptionResponse(
            String code, String message, Exception ex) {
        HttpStatus status = code.equals(ExceptionUtils.ENTITY_NOT_FOUND) ?
                HttpStatus.NOT_FOUND : HttpStatus.UNPROCESSABLE_ENTITY;
        return exceptionResponse(status, code, message, ex);
    }

    private ResponseEntity<ExceptionResponse> apiValidationExceptionResponse(String message, Exception ex) {
        String code = "API Validation Error";
        return exceptionResponse(HttpStatus.BAD_REQUEST, code, message, ex);
    }

    private ResponseEntity<ExceptionResponse> exceptionResponse(
            HttpStatus status, String code, String message, Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stacktrace = sw.toString();
        logger.error(message, ex);
        ExceptionResponse exceptionResponse = ImmutableExceptionResponse.builder()
                .code(code).message(message).stacktrace(stacktrace).build();
        return new ResponseEntity<>(exceptionResponse, status);
    }
}