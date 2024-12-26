package kz.jarkyn.backend.core.mapper;

import java.util.List;

public interface RequestResponseMapper<E, Q, R> extends RequestMapper<E, Q> {
    R toResponse(E entity);
    List<R> toResponse(List<E> entities);
}
