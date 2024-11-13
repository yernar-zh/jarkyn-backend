package kz.jarkyn.backend.counterparty.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.user.model.UserEntity;

@Entity
@Table(name = "customer")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class CustomerEntity extends CounterpartyEntity {
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "shipping_address")
    private String shippingAddress;
    private Integer discount;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

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

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
