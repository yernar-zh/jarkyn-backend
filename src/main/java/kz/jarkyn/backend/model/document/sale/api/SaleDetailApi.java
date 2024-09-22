
package kz.jarkyn.backend.model.document.sale.api;

import kz.jarkyn.backend.model.common.api.IdNamedApi;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value.Immutable
public interface SaleDetailApi {
    UUID getId();

    String getName();

    IdNamedApi getWarehouse();

    IdNamedApi getCustomer();

    LocalDateTime getMoment();

    String getComment();
    LocalDateTime getShipmentMoment();
    Integer getPlacesQuantity();
}
