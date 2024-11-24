package kz.jarkyn.backend.counterparty.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerSearchResponse implements IdNamedDto {
    private final UUID id;
    private final String name;
    private final String phoneNumber;
    private final String shippingAddress;
    private final Integer discount;
    private final Integer balance;
    private final LocalDateTime firstSale;
    private final LocalDateTime lastSale;
    private final Integer totalSaleCount;
    private final Integer totalSaleAmount;

    public CustomerSearchResponse(
            UUID id, String name, String phoneNumber, String shippingAddress,
            Integer discount, Integer balance, LocalDateTime firstSale, LocalDateTime lastSale,
            Integer totalSaleCount, Integer totalSaleAmount) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.shippingAddress = shippingAddress;
        this.discount = discount;
        this.balance = balance;
        this.firstSale = firstSale;
        this.lastSale = lastSale;
        this.totalSaleCount = totalSaleCount;
        this.totalSaleAmount = totalSaleAmount;
    }

    @Override public UUID getId() {
        return id;
    }

    @Override public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public Integer getDiscount() {
        return discount;
    }

    public Integer getBalance() {
        return balance;
    }

    public LocalDateTime getFirstSale() {
        return firstSale;
    }

    public LocalDateTime getLastSale() {
        return lastSale;
    }

    public Integer getTotalSaleCount() {
        return totalSaleCount;
    }

    public Integer getTotalSaleAmount() {
        return totalSaleAmount;
    }
}
