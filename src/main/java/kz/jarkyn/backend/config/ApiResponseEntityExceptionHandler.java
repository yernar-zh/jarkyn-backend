package kz.jarkyn.backend.config;


import kz.jarkyn.backend.exception.ApiValidationException;
import kz.jarkyn.backend.exception.DataValidationException;
import kz.jarkyn.backend.exception.ExceptionUtils;
import kz.jarkyn.backend.model.common.api.ExceptionApi;
import kz.jarkyn.backend.model.common.api.ImmutableExceptionApi;
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
    public final ResponseEntity<ExceptionApi> handleException(
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

    public ResponseEntity<ExceptionApi> handBindException(HttpMessageNotReadableException ex) {
        return apiValidationExceptionResponse("json error", ex);
    }


    private ResponseEntity<ExceptionApi> handleInternalException(Exception ex) {
        String code = "Internal Server Error";
        String message = "Something went wrong";

        logger.error(message, ex);

        return exceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, code, message, ex);
    }

    private ResponseEntity<ExceptionApi> dataValidationExceptionResponse(
            String code, String message, Exception ex) {
        HttpStatus status = code.equals(ExceptionUtils.NOT_FOUND) ?
                HttpStatus.NOT_FOUND : HttpStatus.UNPROCESSABLE_ENTITY;
        return exceptionResponse(status, code, message, ex);
    }

    private ResponseEntity<ExceptionApi> apiValidationExceptionResponse(String message, Exception ex) {
        String code = "API Validation Error";
        return exceptionResponse(HttpStatus.BAD_REQUEST, code, message, ex);
    }

    private ResponseEntity<ExceptionApi> exceptionResponse(
            HttpStatus status, String code, String message, Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stacktrace = sw.toString();
        logger.error(message, ex);
        ExceptionApi exceptionResponse = ImmutableExceptionApi.builder()
                .code(code).message(message).stacktrace(stacktrace).build();
        return new ResponseEntity<>(exceptionResponse, status);
    }
}