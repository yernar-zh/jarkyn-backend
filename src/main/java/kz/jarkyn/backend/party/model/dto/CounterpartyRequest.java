package kz.jarkyn.backend.party.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceRequest;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableCounterpartyRequest.Builder.class)
public interface CounterpartyRequest extends ReferenceRequest {
    @Nullable String getPhoneNumber();
    String getShippingAddress();
    Integer getDiscount();
}
