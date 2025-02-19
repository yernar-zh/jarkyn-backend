package kz.jarkyn.backend.party.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.NamedDto;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableOrganizationRequest.Builder.class)
public interface OrganizationRequest extends NamedDto {
    Boolean getArchived();
}
