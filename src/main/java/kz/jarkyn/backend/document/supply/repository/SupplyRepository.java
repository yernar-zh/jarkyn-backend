package kz.jarkyn.backend.document.supply.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.document.supply.model.SupplyEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyRepository extends AppRepository<SupplyEntity> {
}