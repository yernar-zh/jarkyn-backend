
package kz.jarkyn.backend.user.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;

@Entity
@Table(name = "users")
public class UserEntity extends AbstractEntity {
    @Column(name = "phone_number")
    private String phoneNumber;
    private String name;
    @Column(name = "auth_token")
    private String authToken;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
