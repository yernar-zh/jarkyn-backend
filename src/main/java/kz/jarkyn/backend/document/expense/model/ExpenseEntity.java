package kz.jarkyn.backend.document.expense.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

@Entity
@Table(name = "expense")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class ExpenseEntity extends DocumentEntity {
}
