
package kz.jarkyn.backend.document.supply.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.NamedDto;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentRequest;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableSupplyRequest.Builder.class)
public interface SupplyRequest extends NamedDto {
    @Nullable String getName();
    IdDto getWarehouse();
    IdDto getCounterparty();
    LocalDateTime getMoment();
    BigDecimal getAmount();
    String getComment();
    Integer getExchangeRate();
    List<ItemRequest> getItems();
    List<PaidDocumentRequest> getOutPaidDocuments();
}
