package kz.jarkyn.backend.model.group.api;

import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GroupListApi extends IdNamedDto {
    List<GroupListApi> getChildren();
}
