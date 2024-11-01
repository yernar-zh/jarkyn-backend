
package kz.jarkyn.backend.model.user;


import jakarta.persistence.*;
import kz.jarkyn.backend.model.common.AbstractEntity;

@Entity
@Table(name = "role_permission")
public class RolePermissionEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;
    @Enumerated(EnumType.STRING)
    private Permission permission;

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
