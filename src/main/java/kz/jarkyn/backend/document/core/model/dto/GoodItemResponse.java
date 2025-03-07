package kz.jarkyn.backend.document.core.model.dto;

import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface GoodItemResponse extends ReferenceResponse {
    BigDecimal getWeight();
}