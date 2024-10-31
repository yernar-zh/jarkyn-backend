
package kz.jarkyn.backend.model.user;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.jarkyn.backend.model.common.AbstractEntity;

@Entity
@Table(name = "users")
public class UserEntity extends AbstractEntity {
    private String name;
    private String authToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
