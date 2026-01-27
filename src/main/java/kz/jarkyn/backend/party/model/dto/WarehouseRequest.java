package kz.jarkyn.backend.party.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.ReferenceRequest;
import kz.jarkyn.backend.good.model.dto.ImmutableWarehouseRequest;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableWarehouseRequest.Builder.class)
public interface WarehouseRequest extends ReferenceRequest {
}
