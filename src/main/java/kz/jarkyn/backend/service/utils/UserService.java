
package kz.jarkyn.backend.service.utils;


import kz.jarkyn.backend.model.user.UserEntity;
import kz.jarkyn.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserEntity findCurrent() {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userId).orElseThrow();
    }

    @Transactional(readOnly = true)
    public UserEntity findByAuthToken(String token) {
        return userRepository.findByAuthToken(token);
    }
}
