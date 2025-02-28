package kz.jarkyn.backend.party.model.dto;

import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

@Value.Immutable
public interface CustomerResponse extends ReferenceResponse {
    String getPhoneNumber();
    String getShippingAddress();
    Integer getDiscount();
}
