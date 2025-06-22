package kz.jarkyn.backend.global.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.ResponseMapper;
import kz.jarkyn.backend.global.model.ImageEntity;
import kz.jarkyn.backend.global.model.dto.ImageResponse;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface ImageMapper extends ResponseMapper<ImageEntity, ImageResponse> {
}
