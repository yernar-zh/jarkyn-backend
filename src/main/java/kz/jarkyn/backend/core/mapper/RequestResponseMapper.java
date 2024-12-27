package kz.jarkyn.backend.core.mapper;

import kz.jarkyn.backend.core.model.AbstractEntity;

import java.util.List;

public interface RequestResponseMapper<E extends AbstractEntity, Q, R> extends RequestMapper<E, Q> {
    R toResponse(E entity);
    List<R> toResponse(List<E> entities);
}
