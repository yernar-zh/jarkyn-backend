package kz.jarkyn.backend.good.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.good.model.SellingPriceEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellingPriceRepository extends AppRepository<SellingPriceEntity> {
    List<SellingPriceEntity> findByGood(GoodEntity good);
}