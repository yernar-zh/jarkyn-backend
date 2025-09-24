package kz.jarkyn.backend.document.expense.model;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import org.immutables.value.internal.$processor$.meta.$GsonMirrors;

import java.math.BigDecimal;

@Entity
@Table(name = "related_expenses")
public class RelatedExpenseEntity extends AbstractEntity {
    @$GsonMirrors.Ignore
    @ManyToOne
    @JoinColumn(name = "expense_id")
    private ExpenseEntity expense;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    private BigDecimal amount;

    public ExpenseEntity getExpense() {
        return expense;
    }

    public void setExpense(ExpenseEntity expense) {
        this.expense = expense;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
