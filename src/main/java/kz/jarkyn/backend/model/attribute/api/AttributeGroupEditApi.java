package kz.jarkyn.backend.model.attribute.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.model.common.api.IdNamedApi;
import kz.jarkyn.backend.model.common.api.NamedApi;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableAttributeGroupEditApi.Builder.class)
public interface AttributeGroupEditApi extends NamedApi {
    List<IdApi> getAttributes();
}
