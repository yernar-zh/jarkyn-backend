
package kz.jarkyn.backend.global.model.dto;

import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

@Value.Immutable
public interface ItemOfExpenditureResponse extends ReferenceResponse {
    String getCode();
}
