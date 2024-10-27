
package kz.jarkyn.backend.service.utils;


import kz.jarkyn.backend.model.user.UserEntity;
import kz.jarkyn.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    public static final UUID SYSTEM_USER_ID = UUID.fromString("fd654f2c-5b7d-11ee-0a80-0730002f50c9");

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserEntity findCurrent() {
        return userRepository.findById(SYSTEM_USER_ID).orElseThrow();
    }
}
