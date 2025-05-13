package kz.jarkyn.backend.party.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;

@Entity
@Table(name = "counterparty")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class CounterpartyEntity extends PartyEntity {
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "shipping_address")
    private String shippingAddress;
    private Integer discount;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }
}
