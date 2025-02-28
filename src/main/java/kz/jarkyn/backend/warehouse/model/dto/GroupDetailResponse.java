
package kz.jarkyn.backend.warehouse.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GroupDetailResponse extends ReferenceResponse {
    @Nullable ReferenceResponse getParent();
    List<ReferenceResponse> getChildren();
}
