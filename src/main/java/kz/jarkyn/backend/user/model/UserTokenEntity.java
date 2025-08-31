package kz.jarkyn.backend.user.model;

import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;

@Entity
@Table(name = "user_token")
public class UserTokenEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private String token;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}