package kz.jarkyn.backend.user.init;

import kz.jarkyn.backend.core.model.dto.ImmutableIdDto;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import kz.jarkyn.backend.global.repository.CurrencyRepository;
import kz.jarkyn.backend.good.model.GroupEntity;
import kz.jarkyn.backend.good.model.dto.ImmutableGroupRequest;
import kz.jarkyn.backend.good.repository.GroupRepository;
import kz.jarkyn.backend.good.service.GroupService;
import kz.jarkyn.backend.party.model.AccountEntity;
import kz.jarkyn.backend.party.model.OrganizationEntity;
import kz.jarkyn.backend.party.model.WarehouseEntity;
import kz.jarkyn.backend.party.model.dto.ImmutableAccountRequest;
import kz.jarkyn.backend.party.model.dto.ImmutableOrganizationRequest;
import kz.jarkyn.backend.party.model.dto.ImmutableWarehouseRequest;
import kz.jarkyn.backend.party.repository.AccountRepository;
import kz.jarkyn.backend.party.repository.OrganizationRepository;
import kz.jarkyn.backend.party.repository.WarehouseRepository;
import kz.jarkyn.backend.party.service.AccountService;
import kz.jarkyn.backend.party.service.OrganizationService;
import kz.jarkyn.backend.party.service.WarehouseService;
import kz.jarkyn.backend.user.model.RoleEntity;
import kz.jarkyn.backend.user.model.dto.ImmutableUserRequest;
import kz.jarkyn.backend.user.model.dto.UserRequest;
import kz.jarkyn.backend.user.repository.RoleRepository;
import kz.jarkyn.backend.user.repository.UserRepository;
import kz.jarkyn.backend.user.service.AuthService;
import kz.jarkyn.backend.user.service.UserService;
import kz.jarkyn.backend.user.spesification.RoleSpecifications;
import kz.jarkyn.backend.user.spesification.UserSpecifications;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OwnerInitializer implements ApplicationRunner {

    private final AuthService authService;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final WarehouseRepository warehouseRepository;
    private final AccountRepository accountRepository;
    private final CurrencyRepository currencyRepository;
    private final GroupRepository groupRepository;
    private final OrganizationService organizationService;
    private final WarehouseService warehouseService;
    private final AccountService accountService;
    private final GroupService groupService;

    public OwnerInitializer(
            AuthService authService,
            RoleRepository roleRepository,
            UserService userService,
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            WarehouseRepository warehouseRepository,
            AccountRepository accountRepository,
            CurrencyRepository currencyRepository,
            GroupRepository groupRepository,
            OrganizationService organizationService,
            WarehouseService warehouseService,
            AccountService accountService,
            GroupService groupService) {
        this.authService = authService;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.warehouseRepository = warehouseRepository;
        this.accountRepository = accountRepository;
        this.currencyRepository = currencyRepository;
        this.groupRepository = groupRepository;
        this.organizationService = organizationService;
        this.warehouseService = warehouseService;
        this.accountService = accountService;
        this.groupService = groupService;
    }

    @Override
    public void run(ApplicationArguments args) {
        authService.setSystemToCurrentSession();

        OrganizationEntity organization = ensureOrganization();
        ensureWarehouses();
        ensureAccounts(organization);
        ensureDefaultGroup();
        ensureOwnerUser();
    }

    private void ensureOwnerUser() {
        RoleEntity ownerRole = roleRepository.findOne(RoleSpecifications.owner()).orElseThrow();
        if (userRepository.exists(UserSpecifications.role(ownerRole))) {
            return;
        }
        UserRequest user = ImmutableUserRequest.of(
                "Ернар", "+7 775 216 6661", "123qwe123",
                ImmutableIdDto.of(ownerRole.getId()));
        userService.createApi(user);
    }

    private OrganizationEntity ensureOrganization() {
        return organizationRepository.findAll().stream()
                .filter(org -> "ИП Жаркын".equals(org.getName()))
                .findFirst()
                .orElseGet(() -> {
                    UUID id = organizationService.createApi(
                            ImmutableOrganizationRequest.builder()
                                    .name("ИП Жаркын")
                                    .build()
                    );
                    return organizationRepository.findById(id).orElseThrow();
                });
    }

    private void ensureWarehouses() {
        ensureWarehouse("Кенжина");
        ensureWarehouse("Барыс");
    }

    private void ensureWarehouse(String name) {
        boolean exists = warehouseRepository.findAll().stream()
                .anyMatch(warehouse -> name.equals(warehouse.getName()));
        if (exists) {
            return;
        }
        warehouseService.createApi(
                ImmutableWarehouseRequest.builder()
                        .name(name)
                        .build()
        );
    }

    private void ensureAccounts(OrganizationEntity organization) {
        CurrencyEntity kzt = currencyRepository.findById(
                UUID.fromString("559109ea-f824-476d-8fa4-9990e53880ff")
        ).orElseThrow();
        CurrencyEntity cny = currencyRepository.findById(
                UUID.fromString("e6a3c207-a358-47bf-ac18-2d09973f3807")
        ).orElseThrow();
        CurrencyEntity usd = currencyRepository.findById(
                UUID.fromString("24f15639-67da-4df4-aab4-56b85c872c3b")
        ).orElseThrow();

        ensureAccount(
                "Каспи Ернар Ж",
                organization,
                "Каспи ",
                "Ернар Ж +7 775 216 6661",
                kzt
        );
        ensureAccount(
                "Наличными Тенге",
                organization,
                "",
                "",
                kzt
        );
        ensureAccount(
                "Наличными Юань",
                organization,
                "",
                "",
                cny
        );
        ensureAccount(
                "Наличными Доллар",
                organization,
                "",
                "",
                usd
        );
    }

    private void ensureAccount(
            String name,
            OrganizationEntity organization,
            String bank,
            String giro,
            CurrencyEntity currency) {
        boolean exists = accountRepository.findAll().stream()
                .anyMatch(account ->
                        name.equals(account.getName())
                                && account.getOrganization() != null
                                && organization.getId().equals(account.getOrganization().getId())
                );
        if (exists) {
            return;
        }
        accountService.createApi(
                ImmutableAccountRequest.builder()
                        .name(name)
                        .organization(ImmutableIdDto.of(organization.getId()))
                        .bank(bank)
                        .giro(giro)
                        .currency(ImmutableIdDto.of(currency.getId()))
                        .build()
        );
    }

    private void ensureDefaultGroup() {
        boolean exists = groupRepository.findAll().stream()
                .anyMatch(group -> "Мотозапчасти".equals(group.getName()));
        if (exists) {
            return;
        }
        groupService.createApi(
                ImmutableGroupRequest.builder()
                        .name("Мотозапчасти")
                        .searchKeywords("")
                        .children(List.of())
                        .build()
        );
    }
}