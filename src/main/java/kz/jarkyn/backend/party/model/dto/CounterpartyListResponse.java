package kz.jarkyn.backend.party.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

@Value.Immutable
public interface CounterpartyListResponse extends ReferenceResponse {
    @Nullable String getPhoneNumber();
    Integer getDiscount();
    String getShippingAddress();
}
