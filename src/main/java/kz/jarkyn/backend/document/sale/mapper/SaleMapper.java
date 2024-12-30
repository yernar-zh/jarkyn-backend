package kz.jarkyn.backend.document.sale.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.document.sale.model.dto.SaleRequest;
import kz.jarkyn.backend.document.sale.model.dto.SaleResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = EntityMapper.class)
public interface SaleMapper extends RequestMapper<SaleEntity, SaleRequest> {
    SaleResponse toResponse(
            SaleEntity entity, List<ItemResponse> items, List<PaidDocumentResponse> paidDocuments);
}
