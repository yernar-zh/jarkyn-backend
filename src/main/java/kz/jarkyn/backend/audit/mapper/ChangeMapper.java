package kz.jarkyn.backend.audit.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import kz.jarkyn.backend.audit.model.dto.EntityChangeResponse;
import kz.jarkyn.backend.audit.model.dto.EntityGroupChangeResponse;
import kz.jarkyn.backend.audit.model.dto.FieldChangeResponse;
import kz.jarkyn.backend.audit.model.dto.MainEntityChangeResponse;
import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.user.model.UserEntity;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface ChangeMapper {
    FieldChangeResponse toFieldChangeResponse(String fieldName, JsonNode before, JsonNode after);
    EntityChangeResponse toEntityChangeResponse(String action, List<FieldChangeResponse> fieldChanges);
    EntityGroupChangeResponse toEntityGroupChangeResponse(
            String entityName, List<EntityChangeResponse> entityChanges);
    MainEntityChangeResponse toMainEntityChange(
            Instant moment, UserEntity user, String action,
            List<FieldChangeResponse> fieldChanges, List<EntityGroupChangeResponse> subEntityGroupChanges);

}
