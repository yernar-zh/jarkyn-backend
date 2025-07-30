package kz.jarkyn.backend.audit.model.dto;

import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;

@Value.Immutable
public interface MainEntityChangeResponse extends EntityChangeResponse {
    Instant getMoment();
    ReferenceResponse getUser();
    List<EntityGroupChangeResponse> getSubEntityGroupChanges();
}
