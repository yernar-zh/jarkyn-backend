package kz.jarkyn.backend.party.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import org.immutables.value.Value;

@Value.Immutable
public interface CustomerResponse extends IdNamedDto {
    String getPhoneNumber();
    String getShippingAddress();
    Integer getDiscount();
    Boolean getArchived();
}
