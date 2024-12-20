package kz.jarkyn.backend.counterparty.repository;

import kz.jarkyn.backend.counterparty.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByOrganizationAndCounterpartyAndCurrency(
            OrganizationEntity organization, CounterpartyEntity counterparty, Currency currency);
    List<AccountEntity> findByCounterparty(CounterpartyEntity counterparty);
}