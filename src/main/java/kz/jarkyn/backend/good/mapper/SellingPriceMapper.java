
package kz.jarkyn.backend.good.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.good.model.SellingPriceEntity;
import kz.jarkyn.backend.good.model.dto.SellingPriceRequest;
import org.mapstruct.Mapper;

@Mapper(uses = EntityMapper.class)
public interface SellingPriceMapper extends RequestMapper<SellingPriceEntity, SellingPriceRequest> {
}
