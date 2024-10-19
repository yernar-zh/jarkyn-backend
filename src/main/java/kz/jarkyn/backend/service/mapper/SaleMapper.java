package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.model.document.common.api.ItemResponse;
import kz.jarkyn.backend.model.document.payment.PaymentInForSaleEntity;
import kz.jarkyn.backend.model.document.sale.SaleEntity;
import kz.jarkyn.backend.model.document.sale.api.SaleDetailResponse;
import kz.jarkyn.backend.model.document.sale.api.SaleRequest;
import kz.jarkyn.backend.model.group.GroupEntity;
import kz.jarkyn.backend.model.group.api.GroupRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(uses = EntityMapper.class)
public abstract class SaleMapper {
    public abstract SaleDetailResponse toDetailResponse(
            SaleEntity entity, List<ItemResponse> items, List<PaymentInForSaleEntity> payments);
    public abstract SaleEntity toEntity(SaleRequest request);
    public abstract void editEntity(@MappingTarget SaleEntity entity, SaleRequest request);
}
