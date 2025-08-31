package kz.jarkyn.backend.party.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.party.model.CounterpartyEntity;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface CounterpartyRepository extends AppRepository<CounterpartyEntity> {
}