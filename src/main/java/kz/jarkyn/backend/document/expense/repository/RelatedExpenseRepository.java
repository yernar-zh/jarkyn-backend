package kz.jarkyn.backend.document.expense.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.document.expense.model.RelatedExpenseEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatedExpenseRepository extends AppRepository<RelatedExpenseEntity> {
}