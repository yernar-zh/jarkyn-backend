package kz.jarkyn.backend.counterparty.repository;

import kz.jarkyn.backend.counterparty.model.OrganizationEntity;
import kz.jarkyn.backend.document.model.DocumentEntity;
import kz.jarkyn.backend.document.model.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, UUID> {
}