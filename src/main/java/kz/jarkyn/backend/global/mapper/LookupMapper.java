package kz.jarkyn.backend.global.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.global.model.dto.DefaultLookupResponse;
import kz.jarkyn.backend.party.model.dto.CounterpartyResponse;
import kz.jarkyn.backend.party.model.dto.OrganizationResponse;
import kz.jarkyn.backend.good.model.dto.GroupDetailResponse;
import kz.jarkyn.backend.party.model.dto.WarehouseResponse;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface LookupMapper {
    DefaultLookupResponse toDefaultResponse(
            OrganizationResponse organization, WarehouseResponse warehouse,
            CounterpartyResponse supplyCounterparty, EnumTypeResponse supplyCurrency,
            GroupDetailResponse group);

}
