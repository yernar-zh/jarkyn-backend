package kz.jarkyn.backend.warehouse.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.NamedDto;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableWarehouseRequest.Builder.class)
public interface WarehouseRequest extends NamedDto {
    Boolean getArchived();
}
