package kz.jarkyn.backend.party.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

@Value.Immutable
public interface CounterpartyResponse extends ReferenceResponse {
    @Nullable String getPhoneNumber();
    String getShippingAddress();
    Integer getDiscount();
}
