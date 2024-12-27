
package kz.jarkyn.backend.document.supply.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface SupplyResponse extends DocumentResponse {
    IdNamedDto getWarehouse();
    IdNamedDto getCounterparty();
    List<ItemResponse> getItems();
    List<PaidDocumentResponse> getPaidDocuments();
}