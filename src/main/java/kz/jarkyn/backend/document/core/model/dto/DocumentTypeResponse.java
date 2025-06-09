
package kz.jarkyn.backend.document.core.model.dto;

import org.immutables.value.Value;

@Value.Immutable
public interface DocumentTypeResponse {
    String getName();
    String getCode();
}
