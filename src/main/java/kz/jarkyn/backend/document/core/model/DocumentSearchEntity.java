package kz.jarkyn.backend.document.core.model;


import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity_;
import kz.jarkyn.backend.global.model.CoverageEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "document_search")
@PrimaryKeyJoinColumn(name = AbstractEntity_.ID)
public class DocumentSearchEntity extends DocumentEntity {
    BigDecimal paidAmount;
    BigDecimal notPaidAmount;
    @ManyToOne
    @JoinColumn(name = "paidCoverage_id")
    CoverageEntity paidCoverage;

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getNotPaidAmount() {
        return notPaidAmount;
    }

    public void setNotPaidAmount(BigDecimal notPaidAmount) {
        this.notPaidAmount = notPaidAmount;
    }

    public CoverageEntity getPaidCoverage() {
        return paidCoverage;
    }

    public void setPaidCoverage(CoverageEntity paidCoverage) {
        this.paidCoverage = paidCoverage;
    }
}
