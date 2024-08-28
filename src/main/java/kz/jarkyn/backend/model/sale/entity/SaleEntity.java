package kz.jarkyn.backend.model.sale.entity;


import kz.jarkyn.backend.model.counterparty.CounterpartyEntity;
import kz.jarkyn.backend.model.document.entity.DocumentEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "document")
public class SaleEntity extends DocumentEntity {
    private CounterpartyEntity counterparty;
    private LocalDateTime shipmentMoment;
    private Integer placesQuantity;
    private Integer productsTotalAmount;
    private Integer discountAmount;
    private Integer totalAmount;
}
