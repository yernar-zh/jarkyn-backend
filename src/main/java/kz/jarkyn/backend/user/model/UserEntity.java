
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
    @Column(nullable = false)
    private String passwordHash;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }
}
