package kz.jarkyn.backend.user.init;

import kz.jarkyn.backend.core.model.dto.ImmutableIdDto;
import kz.jarkyn.backend.user.model.RoleEntity;
import kz.jarkyn.backend.user.model.UserEntity;
import kz.jarkyn.backend.user.model.dto.ImmutableUserRequest;
import kz.jarkyn.backend.user.model.dto.UserRequest;
import kz.jarkyn.backend.user.repository.RoleRepository;
import kz.jarkyn.backend.user.service.UserService;
import kz.jarkyn.backend.user.spesification.RoleSpecifications;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OwnerInitializer implements ApplicationRunner {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public OwnerInitializer(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        UserEntity systemUser = userService.findSystem();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(systemUser.getId(), null, List.of()));

        RoleEntity ownerRole = roleRepository.findOne(RoleSpecifications.owner()).orElseThrow();

        UserRequest user = ImmutableUserRequest.of(
                "Ернар", "+7 775 216 6661", "123qwe123",
                ImmutableIdDto.of(ownerRole.getId()));

        userService.createApi(user);
    }
}