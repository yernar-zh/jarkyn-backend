package kz.jarkyn.backend.document.sale.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.payment.model.PaymentInForSaleEntity;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.document.sale.model.dto.SaleDetailResponse;
import kz.jarkyn.backend.document.sale.model.dto.SaleRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = EntityMapper.class)
public abstract class SaleMapper extends RequestMapper<SaleEntity, SaleRequest> {
    public abstract SaleDetailResponse toDetailResponse(
            SaleEntity entity, List<ItemResponse> items, List<PaymentInForSaleEntity> payments);
    public abstract SaleEntity toEntity(SaleRequest request);
}
