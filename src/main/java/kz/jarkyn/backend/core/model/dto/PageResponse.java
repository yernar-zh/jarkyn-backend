package kz.jarkyn.backend.core.model.dto;

import java.util.List;

public interface PageResponse<T> {
    List<T> getRow();
    Integer getPageFirst();
    Integer getPageSize();
    Integer getTotalCount();
}
