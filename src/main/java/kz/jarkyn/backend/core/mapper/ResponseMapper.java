package kz.jarkyn.backend.core.mapper;

import kz.jarkyn.backend.core.model.AbstractEntity;

import java.util.List;

public interface ResponseMapper<E extends AbstractEntity, R> {
    R toResponse(E entity);
    List<R> toResponse(List<E> entities);
}
