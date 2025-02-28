package kz.jarkyn.backend.party.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.ReferenceRequest;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableCustomerRequest.Builder.class)
public interface CustomerRequest extends ReferenceRequest {
    String getPhoneNumber();
    String getShippingAddress();
    Integer getDiscount();
}
