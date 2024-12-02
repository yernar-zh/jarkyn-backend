package kz.jarkyn.backend.core.service.mapper;

import org.mapstruct.MappingTarget;

import java.util.List;

public abstract class RequestResponseMapper<E, Q, R> {
    public abstract R toResponse(E entity);
    public abstract E toEntity(Q request);
    public abstract void editEntity(@MappingTarget E entity, Q request);
    public abstract List<R> toResponse(List<E> entities);
}
