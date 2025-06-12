
package kz.jarkyn.backend.core.model.dto;

import org.immutables.value.Value;

@Value.Immutable
public interface EnumTypeResponse extends ReferenceResponse {
    String getCode();
}
