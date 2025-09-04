package kz.jarkyn.backend.user.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import kz.jarkyn.backend.user.mapper.SessionMapper;
import kz.jarkyn.backend.user.model.RoleEntity;
import kz.jarkyn.backend.user.model.SessionEntity;
import kz.jarkyn.backend.user.model.UserEntity;
import kz.jarkyn.backend.user.model.dto.AccessTokenResponse;
import kz.jarkyn.backend.user.model.dto.LoginResponse;
import kz.jarkyn.backend.user.repository.RoleRepository;
import kz.jarkyn.backend.user.repository.SessionRepository;
import kz.jarkyn.backend.user.repository.UserRepository;
import kz.jarkyn.backend.user.spesification.RoleSpecifications;
import kz.jarkyn.backend.user.spesification.SessionSpecifications;
import kz.jarkyn.backend.user.spesification.UserSpecifications;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AuthService {
    private final SecureRandom secureRandom = new SecureRandom();
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SessionMapper sessionMapper;
    private final PasswordEncoder passwordEncoder;
    @Value("${security.jwt.secret}")
    private String secret;
    @Value("${security.jwt.refresh-ttl-minutes}")
    private long refreshTtlMinutes;
    @Value("${security.jwt.access-ttl-minutes}")
    private long accessTtlMinutes;
    private SecretKey key;

    public AuthService(
            SessionRepository sessionRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            SessionMapper sessionMapper,
            RoleRepository roleRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionMapper = sessionMapper;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Transactional(readOnly = true)
    public UserEntity getCurrentUser() {
        return getCurrentSession().getUser();
    }

    @Transactional(readOnly = true)
    public SessionEntity getCurrentSession() {
        UUID sessionId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return sessionRepository.findById(sessionId).orElseThrow();
    }

    @Transactional(readOnly = true)
    public void setCurrentSession(String accessToken) {
        String subject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .getSubject();

        SessionEntity session = sessionRepository.findById(UUID.fromString(subject))
                .orElseThrow(() -> new BadCredentialsException("Bad creds"));
        if (!validate(session)) {
            throw new BadCredentialsException("Bad creds");
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(session.getId(), null, List.of()));
    }

    @Transactional(readOnly = true)
    public void setSystemToCurrentSession() {
        RoleEntity role = roleRepository.findOne(RoleSpecifications.system()).orElseThrow();
        UserEntity user = userRepository.findOne(UserSpecifications.role(role)).orElseThrow();
        SessionEntity session = sessionRepository.findOne(SessionSpecifications.user(user)).orElseThrow();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(session.getId(), null, List.of()));
    }

    @Transactional
    public LoginResponse login(String phoneNumber, String password, String ip, String userAgent) {
        UserEntity user = userRepository.findOne(UserSpecifications.phoneNumber(phoneNumber))
                .orElseThrow(() -> new BadCredentialsException("Bad Credentials"));
        if (user.getArchived() || !passwordEncoder.matches(password, user.getPasswordHash()))
            throw new BadCredentialsException("Bad creds");

        byte[] b = new byte[32];
        secureRandom.nextBytes(b);
        String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
        String refreshTokenHash = DigestUtils.sha256Hex(refreshToken);

        SessionEntity session = new SessionEntity();
        session.setUser(user);
        session.setRefreshTokenHash(refreshTokenHash);
        session.setIp(ip);
        session.setUserAgent(userAgent);
        session.setExpiresAt(Instant.now().plus(refreshTtlMinutes, ChronoUnit.MINUTES));
        session.setVersion(0);
        sessionRepository.save(session);

        AccessTokenResponse accessToken = generateAccessToken(session);
        return sessionMapper.toLoginResponse(refreshToken, accessToken);
    }

    @Transactional
    public AccessTokenResponse refresh(String refreshToken) {
        String refreshTokenHash = DigestUtils.sha256Hex(refreshToken);
        SessionEntity session = sessionRepository
                .findOne(SessionSpecifications.refreshTokenHash(refreshTokenHash))
                .orElseThrow(() -> new BadCredentialsException("Bad creds"));
        if (!validate(session)) {
            throw new BadCredentialsException("Bad creds");
        }
        return generateAccessToken(session);
    }

    @Transactional
    public void logout() {
        SessionEntity current = getCurrentSession();
        current.setRevokedAt(Instant.now());
    }

    @Transactional
    public void revokeAllExceptCurrent(UserEntity user) {
        SessionEntity current = getCurrentSession();
        for (SessionEntity session : sessionRepository.findAll(SessionSpecifications.user(user))) {
            if (session.equals(current)) continue;
            session.setRevokedAt(Instant.now());
        }
    }

    private AccessTokenResponse generateAccessToken(SessionEntity session) {
        Instant now = Instant.now();
        String accessToken = Jwts.builder()
                .subject(Objects.requireNonNull(session.getId()).toString())
                .claim("sessionVersion", session.getVersion())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlMinutes * 60)))
                .signWith(key)
                .compact();
        return sessionMapper.toAccessTokenResponse(accessToken, session.getUser().getRole());
    }

    private boolean validate(SessionEntity session) {
        Instant now = Instant.now();
        if (session.getExpiresAt().compareTo(now) < 0) return false;
        return session.getRevokedAt() == null;
    }
}