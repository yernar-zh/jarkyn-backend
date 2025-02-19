package kz.jarkyn.backend.party.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.NamedDto;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableAccountRequest.Builder.class)
public interface AccountRequest extends NamedDto {
    IdDto getOrganization();
    String getBank();
    String getGiro();
    IdDto getCurrency();
}
