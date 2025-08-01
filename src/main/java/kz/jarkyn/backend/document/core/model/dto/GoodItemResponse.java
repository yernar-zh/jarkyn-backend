package kz.jarkyn.backend.document.core.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import kz.jarkyn.backend.global.model.dto.ImageResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface GoodItemResponse extends ReferenceResponse {
    @Nullable ImageResponse getImage();
    BigDecimal getWeight();
}