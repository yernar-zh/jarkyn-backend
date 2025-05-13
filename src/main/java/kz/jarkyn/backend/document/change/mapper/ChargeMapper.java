package kz.jarkyn.backend.document.change.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.document.change.model.ChargeEntity;
import kz.jarkyn.backend.document.change.model.dto.ChargeDocumentResponse;
import kz.jarkyn.backend.document.change.model.dto.ChargeRequest;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = EntityMapper.class)
public interface ChargeMapper extends RequestMapper<ChargeEntity, ChargeRequest> {
    PaymentInResponse toResponse(ChargeEntity entity, List<ChargeDocumentResponse> chargeDocuments);
}
