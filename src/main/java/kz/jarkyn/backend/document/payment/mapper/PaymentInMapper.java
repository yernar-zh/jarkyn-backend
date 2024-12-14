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
public abstract class PaymentInMapper extends RequestMapper<PaymentInEntity, PaymentInRequest> {
    public abstract PaymentInDetailResponse toResponse(PaymentInEntity entity, List<PaidDocumentResponse> paidDocuments);
}
