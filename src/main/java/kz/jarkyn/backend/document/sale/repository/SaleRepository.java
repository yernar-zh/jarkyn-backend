package kz.jarkyn.backend.document.sale.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends AppRepository<SaleEntity> {
}
