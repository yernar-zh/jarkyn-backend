
package kz.jarkyn.backend.user.service;


import kz.jarkyn.backend.user.model.PermissionEnum;
import kz.jarkyn.backend.user.model.RoleEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {
    @Transactional(readOnly = true)
    public List<PermissionEnum> findPermission(RoleEnum role) {
        return role.getPermissions();
    }

}
