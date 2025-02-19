
package kz.jarkyn.backend.warehouse.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.warehouse.model.SellingPriceEntity;
import kz.jarkyn.backend.warehouse.model.dto.SellingPriceRequest;
import org.mapstruct.Mapper;

@Mapper(uses = EntityMapper.class)
public interface SellingPriceMapper extends RequestMapper<SellingPriceEntity, SellingPriceRequest> {
}
