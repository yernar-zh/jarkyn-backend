package kz.jarkyn.backend.document.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import org.immutables.value.Value;

@Value.Immutable
public interface GoodItemResponse extends IdNamedDto {
    Integer getRemain();
    Boolean getArchived();
}
