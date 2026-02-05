package kz.jarkyn.backend.document.sale.model.dto;

import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface SaleListResponse extends DocumentResponse {
    ReferenceResponse getWarehouse();
    ReferenceResponse getCounterparty();
    BigDecimal getPaidAmount();
    BigDecimal getNotPaidAmount();
    BigDecimal getOverheadCostAmount();
}
