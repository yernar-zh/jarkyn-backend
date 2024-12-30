
package kz.jarkyn.backend.document.payment.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import kz.jarkyn.backend.document.payment.model.ItemOfExpenditure;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface PaymentOutListResponse extends DocumentResponse {
    IdDto getAccount();
    @Nullable IdDto getCounterparty();
    @Nullable String getReceiptNumber();
    ItemOfExpenditure getItemOfExpenditure();
    @Nullable String getPurpose();
}
