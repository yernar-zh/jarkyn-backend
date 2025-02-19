package kz.jarkyn.backend.core.mapper;

import kz.jarkyn.backend.core.model.AbstractEntity;

public interface RequestResponseMapper<E extends AbstractEntity, Q, R>
        extends RequestMapper<E, Q>, ResponseMapper<E, R> {
}
