package kz.jarkyn.backend.model.attribute.api;

import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface AttributeGroupResponse extends IdNamedDto {
    List<AttributeResponse> getAttributes();
}
