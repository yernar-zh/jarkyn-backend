package kz.jarkyn.backend.document.expense.repository;

import kz.jarkyn.backend.core.repository.AppRepository;
import kz.jarkyn.backend.document.expense.model.ExpenseEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends AppRepository<ExpenseEntity> {
}