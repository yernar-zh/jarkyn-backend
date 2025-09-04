package kz.jarkyn.backend.user.init;

import kz.jarkyn.backend.core.model.dto.ImmutableIdDto;
import kz.jarkyn.backend.user.model.RoleEntity;
import kz.jarkyn.backend.user.model.dto.ImmutableUserRequest;
import kz.jarkyn.backend.user.model.dto.UserRequest;
import kz.jarkyn.backend.user.repository.RoleRepository;
import kz.jarkyn.backend.user.service.AuthService;
import kz.jarkyn.backend.user.service.UserService;
import kz.jarkyn.backend.user.spesification.RoleSpecifications;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class OwnerInitializer implements ApplicationRunner {

    private final AuthService authService;
    private final RoleRepository roleRepository;
    private final UserService userService;

    public OwnerInitializer(
            AuthService authService,
            RoleRepository roleRepository,
            UserService userService) {
        this.authService = authService;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        authService.setSystemToCurrentSession();

        RoleEntity ownerRole = roleRepository.findOne(RoleSpecifications.owner()).orElseThrow();
        UserRequest user = ImmutableUserRequest.of(
                "Ернар", "+7 775 216 6661", "123qwe123",
                ImmutableIdDto.of(ownerRole.getId()));
        userService.createApi(user);
    }
}