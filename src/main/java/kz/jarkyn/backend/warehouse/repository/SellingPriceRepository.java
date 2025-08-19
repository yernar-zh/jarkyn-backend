package kz.jarkyn.backend.warehouse.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.warehouse.model.GoodEntity;
import kz.jarkyn.backend.warehouse.model.SellingPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SellingPriceRepository extends AppRepository<SellingPriceEntity> {
    List<SellingPriceEntity> findByGood(GoodEntity good);
}