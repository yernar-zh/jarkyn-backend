package kz.jarkyn.backend.user.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.ReferenceRequest;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableUserRequest.Builder.class)
public interface UserRequest extends ReferenceRequest {
    String getPhoneNumber();
    String getPassword();
    IdDto getRole();
}
