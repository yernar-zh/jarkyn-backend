package kz.jarkyn.backend.core.mapper;

import org.mapstruct.MapperConfig;

@MapperConfig(uses = {EntityMapper.class})
public interface BaseMapperConfig {
}