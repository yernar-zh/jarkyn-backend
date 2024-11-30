package kz.jarkyn.backend.core.model.dto;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface PageResponse<T> {
    List<T> getRow();
    Page getPage();

    @Value.Immutable
    interface Page {
        Integer getFirst();
        Integer getSize();
        Integer getTotalCount();
    }
}
