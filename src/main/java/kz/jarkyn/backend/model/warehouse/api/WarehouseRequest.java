package kz.jarkyn.backend.model.warehouse.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.model.common.dto.NamedDto;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableWarehouseRequest.Builder.class)
public interface WarehouseRequest extends NamedDto {
}
