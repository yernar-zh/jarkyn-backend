package kz.jarkyn.backend.party.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.party.model.OrganizationEntity;
import kz.jarkyn.backend.party.model.dto.OrganizationRequest;
import kz.jarkyn.backend.party.model.dto.OrganizationResponse;
import org.mapstruct.Mapper;

@Mapper(uses = EntityMapper.class)
public interface OrganizationMapper extends RequestResponseMapper<OrganizationEntity, OrganizationRequest, OrganizationResponse> {
}
