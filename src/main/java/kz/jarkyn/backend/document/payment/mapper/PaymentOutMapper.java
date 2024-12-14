package kz.jarkyn.backend.document.payment.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.document.payment.model.PaymentOutEntity;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutDetailResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = EntityMapper.class)
public abstract class PaymentOutMapper extends RequestMapper<PaymentOutEntity, PaymentOutRequest> {
    public abstract PaymentOutDetailResponse toResponse(PaymentOutEntity entity, List<PaidDocumentResponse> paidDocuments);
}
