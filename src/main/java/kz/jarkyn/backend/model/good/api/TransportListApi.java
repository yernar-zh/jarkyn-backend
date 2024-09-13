package kz.jarkyn.backend.model.good.api;

import org.immutables.value.Value;

import java.util.List;
import java.util.UUID;

@Value.Immutable
public interface TransportListApi {
    UUID getId();
    String getName();
    List<TransportListApi> getChildren();
}
