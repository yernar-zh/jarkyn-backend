
package kz.jarkyn.backend.user.service;


import kz.jarkyn.backend.user.model.Permission;
import kz.jarkyn.backend.user.model.RoleEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {
    @Transactional(readOnly = true)
    public List<Permission> findPermission(RoleEntity role) {
        return null;
    }

}
