package kz.jarkyn.backend.global.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.global.model.ImageEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends AppRepository<ImageEntity> {
}