
package kz.jarkyn.backend.document.supply.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentRequest;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableSupplyRequest.Builder.class)
public interface SupplyRequest extends DocumentRequest {
    IdDto getWarehouse();
    IdDto getCounterparty();
    List<ItemRequest> getItems();
}
