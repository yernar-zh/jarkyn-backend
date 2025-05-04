package kz.jarkyn.backend.global.repository;

import kz.jarkyn.backend.global.model.CurrencyEntity;
import kz.jarkyn.backend.global.model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, UUID> {
}