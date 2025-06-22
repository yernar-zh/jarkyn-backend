package kz.jarkyn.backend.document.change.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.document.change.model.OpexEntity;
import kz.jarkyn.backend.document.change.model.dto.ChargeDocumentResponse;
import kz.jarkyn.backend.document.change.model.dto.ChargeRequest;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface ChargeMapper extends RequestMapper<OpexEntity, ChargeRequest> {
    PaymentInResponse toResponse(OpexEntity entity, List<ChargeDocumentResponse> chargeDocuments);
}
