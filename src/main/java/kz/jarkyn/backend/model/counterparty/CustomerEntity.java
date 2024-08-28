package kz.jarkyn.backend.model.counterparty;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "сustomer")
public class CustomerEntity extends CounterpartyEntity {
    private String city;
    private String phoneNumber;
    private String shippingAddress;
    private Integer discount;
}
