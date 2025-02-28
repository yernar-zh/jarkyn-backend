
package kz.jarkyn.backend.user.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.ReferenceEntity;
import kz.jarkyn.backend.party.model.PartyEntity;

@Entity
@Table(name = "users")
public class UserEntity extends ReferenceEntity {
    @ManyToOne
    @JoinColumn(name = "counterparty_id")
    private PartyEntity counterparty;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "auth_token")
    private String authToken;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    public PartyEntity getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(PartyEntity counterparty) {
        this.counterparty = counterparty;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }
}
