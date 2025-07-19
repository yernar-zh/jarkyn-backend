package kz.jarkyn.backend.audit.mapper;

import kz.jarkyn.backend.audit.model.AuditType;
import kz.jarkyn.backend.audit.model.dto.ChangeGroupResponse;
import kz.jarkyn.backend.audit.model.dto.EntityChangeResponse;
import kz.jarkyn.backend.audit.model.dto.FieldChangeResponse;
import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.user.model.UserEntity;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper(config = BaseMapperConfig.class)
public interface ChangeMapper {
    FieldChangeResponse toFieldChangeResponse(String fieldName, String before, String after);
    EntityChangeResponse toEntityChangeResponse(
            EntityChangeResponse.Type type, String entityName, UUID entityId,
            List<FieldChangeResponse> fieldChanges);
    ChangeGroupResponse toGroupResponse(
            Instant moment, UserEntity user, AuditType type,
            List<EntityChangeResponse> entityChanges);

}
