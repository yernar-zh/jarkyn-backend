
package kz.jarkyn.backend.model.document.common.api;

import kz.jarkyn.backend.model.common.api.IdNamedApi;
import org.immutables.value.Value;

@Value.Immutable
public interface ItemApi {
    IdNamedApi getGood();
    Integer getPrice();
    Integer getQuantity();
    Integer getStockQuantity();
}
