package kz.jarkyn.backend.document.payment.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.document.payment.model.PaymentInEntity;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInDetailResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = EntityMapper.class)
public interface PaymentInMapper extends RequestMapper<PaymentInEntity, PaymentInRequest> {
    PaymentInDetailResponse toResponse(PaymentInEntity entity, List<PaidDocumentResponse> paidDocuments);
}
