
package kz.jarkyn.backend.model.document.common.api;

import kz.jarkyn.backend.model.common.api.IdNamedApi;
import kz.jarkyn.backend.model.document.common.ItemEntity;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value.Immutable
public interface DocumentApi extends IdNamedApi {
    IdNamedApi getWarehouse();
    IdNamedApi getCustomer();
    LocalDateTime getMoment();
    String getState();
    Integer getAmount();
    String getComment();
}
