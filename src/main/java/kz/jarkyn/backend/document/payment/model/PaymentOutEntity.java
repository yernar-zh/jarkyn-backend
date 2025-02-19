package kz.jarkyn.backend.document.payment.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.party.model.AccountEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

@Entity
@Table(name = "payment_out")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class PaymentOutEntity extends DocumentEntity {
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
    private String receiptNumber;
    @Enumerated(EnumType.STRING)
    private ItemOfExpenditure itemOfExpenditure;
    private String purpose;

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public ItemOfExpenditure getItemOfExpenditure() {
        return itemOfExpenditure;
    }

    public void setItemOfExpenditure(ItemOfExpenditure itemOfExpenditure) {
        this.itemOfExpenditure = itemOfExpenditure;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
