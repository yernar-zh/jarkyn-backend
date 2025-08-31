package kz.jarkyn.backend.user.service;


import kz.jarkyn.backend.audit.repository.AuditRepository;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.audit.specifications.AuditSpecifications;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.user.mapper.UserMapper;
import kz.jarkyn.backend.user.model.RoleEntity;
import kz.jarkyn.backend.user.model.UserEntity;
import kz.jarkyn.backend.user.model.UserEntity_;
import kz.jarkyn.backend.user.model.UserTokenEntity;
import kz.jarkyn.backend.user.model.dto.UserRequest;
import kz.jarkyn.backend.user.model.dto.UserResponse;
import kz.jarkyn.backend.user.repository.RoleRepository;
import kz.jarkyn.backend.user.repository.UserRepository;
import kz.jarkyn.backend.user.repository.UserTokenRepository;
import kz.jarkyn.backend.user.spesification.RoleSpecifications;
import kz.jarkyn.backend.user.spesification.UserSpecifications;
import kz.jarkyn.backend.user.spesification.UserTokenSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuditService auditService;
    private final SearchFactory searchFactory;
    private final AuditRepository auditRepository;
    private final UserTokenRepository userTokenRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            AuditService auditService,
            SearchFactory searchFactory,
            AuditRepository auditRepository,
            UserTokenRepository userTokenRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
        this.auditRepository = auditRepository;
        this.userTokenRepository = userTokenRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserResponse findApiById(UUID id) {
        UserEntity user = userRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<UserEntity> attributes = CriteriaAttributes.<UserEntity>builder()
                .add("id", (root) -> root.get(UserEntity_.id))
                .add("name", (root) -> root.get(UserEntity_.name))
                .add("archived", (root) -> root.get(UserEntity_.archived))
                .build();
        Search<UserResponse> search = searchFactory.createCriteriaSearch(
                UserResponse.class, QueryParams.NAME_SEARCH, QueryParams.Sort.NAME_ASC,
                UserEntity.class, attributes);
        return search.getResult(queryParams);
    }

    @Transactional
    public UserEntity findByAuthToken(String authToken) {
        return userTokenRepository.findOne(Specification.where(UserTokenSpecifications.token(authToken)))
                .map(UserTokenEntity::getUser).orElse(null);
    }

    @Transactional(readOnly = true)
    public UserEntity findCurrent() {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userId).orElseThrow();
    }

    @Transactional(readOnly = true)
    public UserEntity findSystem() {
        RoleEntity role = roleRepository.findOne(RoleSpecifications.system()).orElseThrow();
        return userRepository.findOne(UserSpecifications.role(role)).orElseThrow();
    }

    @Transactional
    public UUID createApi(UserRequest request) {
        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setArchived(false);
        userRepository.save(user);
        auditService.saveEntity(user);
        return user.getId();
    }

    @Transactional
    public void editApi(UUID id, UserRequest request) {
        UserEntity user = userRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        userMapper.editEntity(user, request);
        auditService.saveEntity(user);
    }


    @Transactional
    public UserResponse archive(UUID id) {
        UserEntity user = userRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        user.setArchived(true);
        auditService.archive(user);
        return findApiById(id);
    }

    @Transactional
    public UserResponse unarchive(UUID id) {
        UserEntity user = userRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        user.setArchived(false);
        auditService.unarchive(user);
        return findApiById(id);
    }

    @Transactional
    public void delete(UUID id) {
        UserEntity user = userRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        boolean existsAudit = auditRepository.exists(AuditSpecifications.user(user));
        if (existsAudit) ExceptionUtils.throwRelationDeleteException();
        // TODO remove all tokens
        userRepository.delete(user);
    }
}