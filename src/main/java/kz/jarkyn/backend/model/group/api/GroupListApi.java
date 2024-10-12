package kz.jarkyn.backend.model.group.api;

import kz.jarkyn.backend.model.common.api.IdNamedApi;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GroupListApi extends IdNamedApi {
    List<GroupListApi> getChildren();
}
