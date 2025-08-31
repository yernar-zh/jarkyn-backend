package kz.jarkyn.backend.global.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.global.model.CoverageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CoverageRepository extends AppRepository<CoverageEntity> {
}