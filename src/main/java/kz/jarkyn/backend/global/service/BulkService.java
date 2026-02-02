package kz.jarkyn.backend.global.service;

import kz.jarkyn.backend.global.model.dto.BulkError;
import kz.jarkyn.backend.global.model.dto.BulkResponse;
import kz.jarkyn.backend.global.model.dto.BulkUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Service
public class BulkService {
    public <T> BulkResponse bulkCreate(List<T> requests, Consumer<T> createFn) {
        List<BulkError> errors = new ArrayList<>();
        int success = 0;

        for (int i = 0; i < requests.size(); i++) {
            try {
                createFn.accept(requests.get(i));
                success++;
            } catch (Exception ex) {
                errors.add(ImmutableBulkError.builder()
                        .index(i)
                        .message(errorMessage(ex))
                        .build());
            }
        }

        return ImmutableBulkResponse.builder()
                .total(requests.size())
                .success(success)
                .failed(errors.size())
                .errors(errors)
                .build();
    }

    public <T> BulkResponse bulkUpdate(List<BulkUpdateRequest<T>> requests, BiConsumer<UUID, T> updateFn) {
        List<BulkError> errors = new ArrayList<>();
        int success = 0;

        for (int i = 0; i < requests.size(); i++) {
            BulkUpdateRequest<T> request = requests.get(i);
            try {
                updateFn.accept(request.getId(), request.getRequest());
                success++;
            } catch (Exception ex) {
                errors.add(ImmutableBulkError.builder()
                        .index(i)
                        .message(errorMessage(ex))
                        .build());
            }
        }

        return ImmutableBulkResponse.builder()
                .total(requests.size())
                .success(success)
                .failed(errors.size())
                .errors(errors)
                .build();
    }

    private String errorMessage(Exception ex) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            return "Unexpected error";
        }
        return message;
    }
}
