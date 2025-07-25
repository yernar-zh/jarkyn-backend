package kz.jarkyn.backend.audit.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import kz.jarkyn.backend.audit.model.AuditType;
import kz.jarkyn.backend.audit.model.dto.ChangeEntityResponse;
import kz.jarkyn.backend.audit.model.dto.ChangeGroupResponse;
import kz.jarkyn.backend.audit.model.dto.ChangeFieldResponse;
import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.user.model.UserEntity;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper(config = BaseMapperConfig.class)
public interface ChangeMapper {
    ChangeFieldResponse toFieldChangeResponse(String fieldName, JsonNode before, JsonNode after);
    ChangeEntityResponse toEntityChangeResponse(
            AuditType type, String entityName, UUID entityId,
            List<ChangeFieldResponse> fieldChanges);
    ChangeGroupResponse toGroupResponse(
            Instant moment, UserEntity user, ChangeGroupResponse.Type type,
            List<ChangeEntityResponse> entityChanges);

}
