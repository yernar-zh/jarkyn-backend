package kz.jarkyn.backend.document.payment.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentRequest;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(config = BaseMapperConfig.class)
public interface PaidDocumentMapper extends RequestMapper<PaidDocumentEntity, PaidDocumentRequest> {
    PaidDocumentResponse toResponse(PaidDocumentEntity entity, BigDecimal notPaidAmount);
}
