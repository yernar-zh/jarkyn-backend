package kz.jarkyn.backend.counterparty.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import org.hibernate.annotations.Immutable;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class CustomerEntity extends CounterpartyEntity {
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "shipping_address")
    private String shippingAddress;
    private Integer discount;
    @Immutable
    @OneToMany(mappedBy = "customer")
    private List<DocumentEntity> documents = new ArrayList<>();

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

    public List<DocumentEntity> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentEntity> documents) {
        this.documents = documents;
    }
}
