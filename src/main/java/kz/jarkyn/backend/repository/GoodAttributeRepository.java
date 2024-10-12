package kz.jarkyn.backend.repository;

import kz.jarkyn.backend.model.good.GoodAttributeEntity;
import kz.jarkyn.backend.model.good.GoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoodAttributeRepository extends JpaRepository<GoodAttributeEntity, UUID> {
    List<GoodAttributeEntity> findByGood(GoodEntity good);
}