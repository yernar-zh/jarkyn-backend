package kz.jarkyn.backend.counterparty.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import org.immutables.value.Value;

@Value.Immutable
public interface OrganizationResponse extends IdNamedDto {
    Boolean getArchived();
}
