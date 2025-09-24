package kz.jarkyn.backend.document.expense.specifications;

import kz.jarkyn.backend.document.expense.model.ExpenseEntity;
import kz.jarkyn.backend.document.expense.model.RelatedExpenseEntity;
import kz.jarkyn.backend.document.expense.model.RelatedExpenseEntity_;
import org.springframework.data.jpa.domain.Specification;

public class RelatedExpenseSpecifications {
    public static Specification<RelatedExpenseEntity> expense(ExpenseEntity expense) {
        return (root, query, cb) -> cb.equal(root.get(RelatedExpenseEntity_.expense), expense);
    }
}