package kz.jarkyn.backend.counterparty.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.NamedDto;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableSupplierRequest.Builder.class)
public interface SupplierRequest extends NamedDto {
}
