
package kz.jarkyn.backend.document.bind.model.dto;

import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface BindDocumentResponse extends IdDto {
    DocumentResponse getPrimaryDocument();
    DocumentResponse getRelatedDocument();
    BigDecimal getPrimaryNotBindAmount();
    BigDecimal getRelatedNotBindAmount();
    BigDecimal getAmount();
}
