package kz.jarkyn.backend.repository;

import kz.jarkyn.backend.model.good.GoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GoodRepository extends JpaRepository<GoodEntity, UUID> {
}