package kz.jarkyn.backend.party.repository;

import kz.jarkyn.backend.party.model.*;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByOrganizationAndCounterpartyAndCurrency(
            OrganizationEntity organization, PartyEntity counterparty, CurrencyEntity currency);
    List<AccountEntity> findByCounterparty(PartyEntity counterparty);
}