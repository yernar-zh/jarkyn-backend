package kz.jarkyn.backend.good.repository;

import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.good.model.SellingPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SellingPriceRepository extends JpaRepository<SellingPriceEntity, UUID> {
    List<SellingPriceEntity> findByGood(GoodEntity good);
}