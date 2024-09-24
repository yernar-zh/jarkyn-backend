package kz.jarkyn.backend.model.document.payment;


import jakarta.persistence.*;
import kz.jarkyn.backend.model.common.AbstractEntity;
import kz.jarkyn.backend.model.document.common.DocumentEntity;

@Entity
@Table(name = "payment_purpose")
public class PaymentPurposeEntity extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private DocumentEntity payment;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    private Integer amount;
}
