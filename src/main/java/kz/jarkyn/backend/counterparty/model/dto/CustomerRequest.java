package kz.jarkyn.backend.counterparty.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.NamedDto;
import org.immutables.value.Value;

import java.time.LocalDateTime;

@Value.Immutable
@JsonDeserialize(builder = ImmutableCustomerRequest.Builder.class)
public interface CustomerRequest extends NamedDto {
    String getPhoneNumber();
    String getShippingAddress();
    Integer getDiscount();
    Boolean getArchived();
}
