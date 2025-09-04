package kz.jarkyn.backend.user.model;

import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;

import java.time.Instant;

@Entity
@Table(name = "session")
public class SessionEntity extends AbstractEntity {
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @Column(name = "refresh_token_hash")
    private String refreshTokenHash;
    private String ip;
    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "expires_at")
    private Instant expiresAt;
    @Column(name = "revoked_at")
    private Instant revokedAt;
    private Integer version;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getRefreshTokenHash() {
        return refreshTokenHash;
    }

    public void setRefreshTokenHash(String refreshTokenHash) {
        this.refreshTokenHash = refreshTokenHash;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}