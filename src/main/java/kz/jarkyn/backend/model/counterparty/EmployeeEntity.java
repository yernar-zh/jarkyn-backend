package kz.jarkyn.backend.model.counterparty;


import jakarta.persistence.*;
import kz.jarkyn.backend.model.common.AbstractEntity_;

@Entity
@Table(name = "employee")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class EmployeeEntity extends CounterpartyEntity {
    @Column(name = "phone_number")
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
