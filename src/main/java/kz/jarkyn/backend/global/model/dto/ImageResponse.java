package kz.jarkyn.backend.global.model.dto;

import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface ImageResponse extends IdDto {
    UUID getOriginalFileId();
    UUID getMediumFileId();
    UUID getThumbnailFileId();
}
