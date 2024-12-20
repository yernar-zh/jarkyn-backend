package kz.jarkyn.backend.core.mapper;

import java.util.List;

public abstract class RequestResponseMapper<E, Q, R> extends RequestMapper<E, Q> {
    public abstract R toResponse(E entity);
    public abstract List<R> toResponse(List<E> entities);
}
