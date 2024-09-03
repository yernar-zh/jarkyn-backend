package kz.jarkyn.backend.model.counterparty;


import jakarta.persistence.*;
import kz.jarkyn.backend.model.common.AbstractEntity;

@Entity
@Table(name = "сustomer")
public class CustomerEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "counterparty_id")
    private CounterpartyEntity counterparty;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "shipping_address")
    private String shippingAddress;
    private Integer discount;

    public CounterpartyEntity getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(CounterpartyEntity counterparty) {
        this.counterparty = counterparty;
    }

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
