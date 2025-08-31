
package kz.jarkyn.backend.user.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.audit.config.AuditIgnore;
import kz.jarkyn.backend.core.model.ReferenceEntity;

@Entity
@Table(name = "users")
public class UserEntity extends ReferenceEntity {
    @Column(name = "phone_number")
    private String phoneNumber;
    @AuditIgnore
    @Column(name = "password")
    private String password;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }
}
