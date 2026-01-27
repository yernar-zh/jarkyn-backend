package kz.jarkyn.backend.good.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.good.model.GoodAttributeEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoodAttributeRepository extends AppRepository<GoodAttributeEntity> {
    Optional<GoodAttributeEntity> findByGoodAndAttribute(GoodEntity good, AttributeEntity attribute);
}