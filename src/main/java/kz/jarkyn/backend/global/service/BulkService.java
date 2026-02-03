package kz.jarkyn.backend.global.service;

import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.exception.DataValidationException;
import kz.jarkyn.backend.core.model.dto.ExceptionResponse;
import kz.jarkyn.backend.core.model.dto.ImmutableExceptionResponse;
import kz.jarkyn.backend.global.model.dto.BulkResponse;
import kz.jarkyn.backend.global.model.dto.BulkUpdateRequest;
import kz.jarkyn.backend.global.model.dto.ImmutableBulkResponse;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class BulkService {
    public <T, R> List<BulkResponse<R>> bulkCreate(List<T> requests, Function<T, R> createFn) {
        List<BulkResponse<R>> results = new ArrayList<>();
        for (T request : requests) {
            try {
                R data = createFn.apply(request);
                results.add(ImmutableBulkResponse.of(true, data, null));
            } catch (Exception ex) {
                results.add(ImmutableBulkResponse.of(false, null, exceptionResponse(ex)));
            }
        }
        return results;
    }

    public <T, R> List<BulkResponse<R>> bulkUpdate(
            List<BulkUpdateRequest<T>> requests,
            BiFunction<UUID, T, R> updateFn) {
        List<BulkResponse<R>> results = new ArrayList<>();
        for (BulkUpdateRequest<T> request : requests) {
            try {
                R data = updateFn.apply(request.getId(), request.getData());
                results.add(ImmutableBulkResponse.of(true, data, null));
            } catch (Exception ex) {
                results.add(ImmutableBulkResponse.of(false, null, exceptionResponse(ex)));
            }
        }
        return results;
    }

    private ExceptionResponse exceptionResponse(Exception ex) {
        String code = "Internal Server Error";
        String message = messageOrDefault(ex.getMessage(), "Something went wrong");

        if (ex instanceof DataValidationException dataValidationException) {
            code = dataValidationException.getCode();
            message = messageOrDefault(dataValidationException.getMessage(), message);
        } else if (ex instanceof ApiValidationException apiValidationException) {
            code = "API Validation Error";
            message = messageOrDefault(apiValidationException.getMessage(), message);
        }

        return ImmutableExceptionResponse.builder()
                .code(code)
                .message(message)
                .stacktrace(stacktrace(ex))
                .build();
    }

    private String stacktrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    private String messageOrDefault(String message, String fallback) {
        if (message == null || message.isBlank()) return fallback;
        return message;
    }
}
