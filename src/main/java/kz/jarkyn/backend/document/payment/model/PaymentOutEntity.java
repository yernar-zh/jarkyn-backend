package kz.jarkyn.backend.document.payment.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.global.model.ItemOfExpenditureEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

@Entity
@Table(name = "payment_out")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class PaymentOutEntity extends DocumentEntity {
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
