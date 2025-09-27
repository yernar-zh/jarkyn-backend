
package kz.jarkyn.backend.document.bind.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value.Immutable
@JsonDeserialize(builder = ImmutableBindDocumentRequest.Builder.class)
public interface BindDocumentRequest extends IdDto {
    @Nullable UUID getId();
    IdDto getRelatedDocument();
    BigDecimal getAmount();
}
