package kz.jarkyn.backend.core.mapper;

import org.mapstruct.MappingTarget;

import java.util.List;

public abstract class RequestMapper<E, Q> {
    public abstract E toEntity(Q request);
    public abstract void editEntity(@MappingTarget E entity, Q request);
}
