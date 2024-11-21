package kz.jarkyn.backend.counterparty.repository;

import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.counterparty.model.CounterpartyEntity;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    List<AccountEntity> findByCounterparty(CounterpartyEntity counterparty);
}