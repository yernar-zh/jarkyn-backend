package kz.jarkyn.backend.warehouse.model.dto;

import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GroupResponse extends ReferenceResponse {
    List<GroupResponse> getChildren();
}
