package kz.jarkyn.backend.core.mapper;

import org.mapstruct.MappingTarget;

import java.util.List;

public interface RequestMapper<E, Q> {
    E toEntity(Q request);
    void editEntity(@MappingTarget E entity, Q request);
}
