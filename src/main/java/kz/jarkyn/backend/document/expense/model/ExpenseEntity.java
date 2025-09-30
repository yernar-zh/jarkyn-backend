package kz.jarkyn.backend.document.expense.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.global.model.ItemOfExpenditureEntity;

@Entity
@Table(name = "expense")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class ExpenseEntity extends DocumentEntity {
    private String receiptNumber;
    @ManyToOne
    @JoinColumn(name = "item_of_expenditure_id")
    private ItemOfExpenditureEntity itemOfExpenditure;
    private String purpose;

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public ItemOfExpenditureEntity getItemOfExpenditure() {
        return itemOfExpenditure;
    }

    public void setItemOfExpenditure(ItemOfExpenditureEntity itemOfExpenditure) {
        this.itemOfExpenditure = itemOfExpenditure;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
