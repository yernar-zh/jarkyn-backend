package kz.jarkyn.backend.document.supply.repository;

import jakarta.persistence.Tuple;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.document.supply.model.SupplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupplyRepository extends JpaRepository<SupplyEntity, UUID> {
}