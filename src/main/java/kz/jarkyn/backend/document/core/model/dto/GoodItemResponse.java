package kz.jarkyn.backend.document.core.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import org.immutables.value.Value;

@Value.Immutable
public interface GoodItemResponse extends IdNamedDto {
    Boolean getArchived();
}
