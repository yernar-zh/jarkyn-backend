package kz.jarkyn.backend.core.model.dto;

import org.immutables.value.Value;

@Value.Immutable
public interface ReferenceResponse extends IdDto {
    String getName();
    Boolean getArchived();
}
