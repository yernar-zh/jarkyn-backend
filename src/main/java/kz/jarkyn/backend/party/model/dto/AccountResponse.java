package kz.jarkyn.backend.party.model.dto;

import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

@Value.Immutable
public interface AccountResponse extends ReferenceResponse, AccountShortResponse {
    String getBank();
    String getGiro();
}
