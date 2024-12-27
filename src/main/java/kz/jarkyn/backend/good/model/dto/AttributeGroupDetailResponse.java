package kz.jarkyn.backend.good.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface AttributeGroupDetailResponse extends IdNamedDto {
    List<AttributeResponse> getAttributes();
}
