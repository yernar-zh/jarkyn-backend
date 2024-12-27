package kz.jarkyn.backend.core.mapper;

import kz.jarkyn.backend.core.model.AbstractEntity;
import org.mapstruct.MappingTarget;

import java.util.List;

public interface RequestMapper<E extends AbstractEntity, Q> {
    E toEntity(Q request);
    void editEntity(@MappingTarget E entity, Q request);
}
