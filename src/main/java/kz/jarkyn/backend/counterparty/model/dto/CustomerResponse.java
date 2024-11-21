package kz.jarkyn.backend.counterparty.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import org.immutables.value.Value;

import java.time.LocalDateTime;

@Value.Immutable
public interface CustomerResponse extends IdNamedDto {
    String getPhoneNumber();
    String getShippingAddress();
    Integer getDiscount();
    Integer getBalance();
    LocalDateTime getFirstSale();
    LocalDateTime getLastSale();
    Integer getTotalSaleCount();
    Integer getTotalSaleAmount();
}
