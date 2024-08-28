package kz.jarkyn.backend.model.counterparty;


import kz.jarkyn.backend.model.common.entity.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "counterparty")
public class CounterpartyEntity extends AbstractEntity {
    private String name;
    private String city;
    private String phoneNumber;
    private String shippingAddress;
    private Integer discount;
}
