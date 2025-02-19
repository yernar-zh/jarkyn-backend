package kz.jarkyn.backend.party.repository;

import kz.jarkyn.backend.party.model.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, UUID> {
}