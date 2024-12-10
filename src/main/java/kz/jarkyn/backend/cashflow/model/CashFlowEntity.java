package kz.jarkyn.backend.cashflow.model;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "cash_flow")
public class CashFlowEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    private BigDecimal amount;

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
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
