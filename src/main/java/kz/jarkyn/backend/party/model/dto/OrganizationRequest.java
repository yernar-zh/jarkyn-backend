package kz.jarkyn.backend.party.model.dto;

import tools.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.ReferenceRequest;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableOrganizationRequest.Builder.class)
public interface OrganizationRequest extends ReferenceRequest {
}
