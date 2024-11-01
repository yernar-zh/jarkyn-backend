
package kz.jarkyn.backend.service;


import kz.jarkyn.backend.model.user.Permission;
import kz.jarkyn.backend.model.user.RoleEntity;
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
