package kz.jarkyn.backend.core.search;

import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;

public interface Search<R> {
    PageResponse<R> getResult(QueryParams queryParams);
}
