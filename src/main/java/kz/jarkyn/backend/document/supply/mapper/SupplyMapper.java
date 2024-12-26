package kz.jarkyn.backend.document.supply.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.supply.model.SupplyEntity;
import kz.jarkyn.backend.document.supply.model.dto.SupplyResponse;
import kz.jarkyn.backend.document.supply.model.dto.SupplyRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = EntityMapper.class)
public interface SupplyMapper extends RequestMapper<SupplyEntity, SupplyRequest> {
    SupplyResponse toDetailResponse(
            SupplyEntity entity, List<ItemResponse> items, List<PaidDocumentResponse> paidDocuments);
}
