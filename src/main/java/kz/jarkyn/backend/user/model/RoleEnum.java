
package kz.jarkyn.backend.user.model;


import java.util.Arrays;
import java.util.List;

public enum RoleEnum {
    SYSTEM("Система", List.of()),
    ADMIN("Админ", Arrays.stream(PermissionEnum.values()).toList()),
    CUSTOMER("Система", List.of());

    private final String name;
    private final List<PermissionEnum> permissions;

    RoleEnum(String name, List<PermissionEnum> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public List<PermissionEnum> getPermissions() {
        return permissions;
    }
}
