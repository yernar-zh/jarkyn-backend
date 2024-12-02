package kz.jarkyn.backend.document.sale.service.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.payment.model.PaymentInForSaleEntity;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.document.sale.model.dto.SaleDetailResponse;
import kz.jarkyn.backend.document.sale.model.dto.SaleRequest;
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
