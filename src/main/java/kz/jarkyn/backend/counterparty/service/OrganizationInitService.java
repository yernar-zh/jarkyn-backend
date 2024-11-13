package kz.jarkyn.backend.counterparty.service;

import kz.jarkyn.backend.account.model.AccountEntity;
import kz.jarkyn.backend.account.repository.AccountRepository;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.OrganizationEntity;
import kz.jarkyn.backend.counterparty.repository.CustomerRepository;
import kz.jarkyn.backend.counterparty.repository.OrganizationRepository;
import kz.jarkyn.backend.user.model.RoleEnum;
import kz.jarkyn.backend.user.model.UserEntity;
import kz.jarkyn.backend.user.repository.UserRepository;
import kz.jarkyn.backend.warehouse.mode.WarehouseEntity;
import kz.jarkyn.backend.warehouse.repository.WarehouseRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrganizationInitService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final WarehouseRepository warehouseRepository;
    private final AuditService auditService;

    public OrganizationInitService(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            CustomerRepository customerRepository,
            AccountRepository accountRepository,
            WarehouseRepository warehouseRepository,
            AuditService auditService
    ) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.warehouseRepository = warehouseRepository;
        this.auditService = auditService;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (!organizationRepository.findAll().isEmpty()) {
            return;
        }

        OrganizationEntity organization = new OrganizationEntity();
        organization.setName("ИП Жырқын");
        organizationRepository.save(organization);

        UserEntity systemUser = new UserEntity();
        systemUser.setId(UUID.fromString("376d6b4e-746b-4ee6-9e6c-8c8223643a7a"));
        systemUser.setName("Система");
        systemUser.setRole(RoleEnum.SYSTEM);
        userRepository.save(systemUser);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(systemUser.getId(), null, null));

        UserEntity adminUser = new UserEntity();
        adminUser.setId(UUID.fromString("f57382c9-35a1-4b64-b3f0-f172489dc90a"));
        adminUser.setName("Ернар");
        adminUser.setPhoneNumber("+77752166661");
        adminUser.setRole(RoleEnum.ADMIN);
        userRepository.save(adminUser);
        auditService.saveChanges(adminUser);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(adminUser.getId(), null, null));

        UserEntity customerUser = new UserEntity();
        customerUser.setId(UUID.fromString("978d5bb5-3e44-4a2a-a36e-8363fa1d5182"));
        customerUser.setName("Оркен Алматы");
        customerUser.setPhoneNumber("+77026705556");
        customerUser.setRole(RoleEnum.CUSTOMER);
        userRepository.save(customerUser);
        auditService.saveChanges(customerUser);

        CustomerEntity customer = new CustomerEntity();
        customer.setId(UUID.fromString("842f873a-4732-4c0e-842d-e88a5ed5221e"));
        customer.setName("Оркен Алматы");
        customer.setUser(customerUser);
        customer.setDiscount(10);
            customer.setShippingAddress("Самовывоз");
        customerRepository.save(customer);
        auditService.saveChanges(customer);

        AccountEntity organizationAccount = new AccountEntity();
        organizationAccount.setId(UUID.fromString("0b956c5e-3e9a-4c59-91c1-cbf59a20f60d"));
        organizationAccount.setCounterparty(organization);
        organizationAccount.setBalance(0);
        organizationAccount.setName("Kaspi Ернар");
        organizationAccount.setGiro("+7 775 216 6661");
        accountRepository.save(organizationAccount);
        auditService.saveChanges(organizationAccount);

        AccountEntity customerAccount = new AccountEntity();
        customerAccount.setId(UUID.fromString("8440789c-ebfe-46f7-833f-6253a2f5e571"));
        customerAccount.setCounterparty(customer);
        customerAccount.setBalance(0);
        accountRepository.save(customerAccount);
        auditService.saveChanges(customerAccount);

        WarehouseEntity warehouse = new WarehouseEntity();
        warehouse.setId(UUID.fromString("523961a7-696d-4779-8bb0-fd327feaecf3"));
        warehouse.setName("Кенжина");
        warehouseRepository.save(warehouse);
        auditService.saveChanges(warehouse);


    }
}