package kz.jarkyn.backend.repository;

import kz.jarkyn.backend.model.good.TransportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransportRepository extends JpaRepository<TransportEntity, UUID> {
}