package kz.jarkyn.backend.counterparty.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.user.model.UserEntity;
import org.hibernate.annotations.Immutable;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "counterparty")
@Inheritance(strategy = InheritanceType.JOINED)
public class CounterpartyEntity extends AbstractEntity {
    private String name;
    @Immutable
    @OneToMany(mappedBy = "counterparty")
    private List<UserEntity> users = new ArrayList<>();
    @Immutable
    @OneToMany(mappedBy = "counterparty")
    private List<AccountEntity> accounts = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public List<AccountEntity> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountEntity> accounts) {
        this.accounts = accounts;
    }
}
