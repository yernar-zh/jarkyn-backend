package kz.jarkyn.backend.counterparty.service;

import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.counterparty.model.OrganizationEntity;
import kz.jarkyn.backend.counterparty.repository.OrganizationRepository;
import kz.jarkyn.backend.user.model.RoleEnum;
import kz.jarkyn.backend.user.model.UserEntity;
import kz.jarkyn.backend.user.repository.UserRepository;
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
    private final AuditService auditService;

    public OrganizationInitService(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            AuditService auditService
    ) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (!organizationRepository.findAll().isEmpty()) {
            return;
        }
        UserEntity systemUser = new UserEntity();
        systemUser.setId(UUID.fromString("376d6b4e-746b-4ee6-9e6c-8c8223643a7a"));
        systemUser.setName("Система");
        systemUser.setRole(RoleEnum.SYSTEM);
        userRepository.save(systemUser);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(systemUser.getId(), null, null));

        OrganizationEntity organization = new OrganizationEntity();
        organization.setName("ИП Жырқын");
        organizationRepository.save(organization);

        UserEntity adminUser = new UserEntity();
        adminUser.setId(UUID.fromString("f57382c9-35a1-4b64-b3f0-f172489dc90a"));
        adminUser.setName("Ернар");
        adminUser.setPhoneNumber("+77752166661");
        adminUser.setRole(RoleEnum.ADMIN);
        userRepository.save(adminUser);
        auditService.saveChanges(adminUser);
    }
}