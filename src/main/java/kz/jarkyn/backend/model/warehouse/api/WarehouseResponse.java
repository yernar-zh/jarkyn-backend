package kz.jarkyn.backend.model.warehouse.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import kz.jarkyn.backend.model.common.dto.NamedDto;
import org.immutables.value.Value;

@Value.Immutable
public interface WarehouseResponse extends IdNamedDto {
}
