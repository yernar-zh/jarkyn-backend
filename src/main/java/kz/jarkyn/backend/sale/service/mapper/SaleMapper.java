package kz.jarkyn.backend.sale.service.mapper;

import kz.jarkyn.backend.core.service.mapper.EntityMapper;
import kz.jarkyn.backend.document.model.dto.ItemResponse;
import kz.jarkyn.backend.payment.model.PaymentInForSaleEntity;
import kz.jarkyn.backend.sale.model.SaleEntity;
import kz.jarkyn.backend.sale.model.dto.SaleDetailResponse;
import kz.jarkyn.backend.sale.model.dto.SaleRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(uses = EntityMapper.class)
public abstract class SaleMapper {
    public abstract SaleDetailResponse toDetailResponse(
            SaleEntity entity, List<ItemResponse> items, List<PaymentInForSaleEntity> payments);
    public abstract SaleEntity toEntity(SaleRequest request);
    public abstract void editEntity(@MappingTarget SaleEntity entity, SaleRequest request);
}
