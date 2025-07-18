package kz.jarkyn.backend.audit.mapper;

import kz.jarkyn.backend.audit.model.dto.ChangeGroupResponse;
import kz.jarkyn.backend.audit.model.dto.ChangeResponse;
import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.user.model.UserEntity;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface ChangeMapper {
    ChangeResponse toResponse(String fieldName, String before, String after);
    ChangeGroupResponse toGroupResponse(
            Instant moment, UserEntity user, String entityName, ChangeGroupResponse.Type type,
            List<ChangeResponse> changes, List<ChangeGroupResponse> subChangeGroups);

}
