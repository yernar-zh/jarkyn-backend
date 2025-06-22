package kz.jarkyn.backend.document.payment.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.document.payment.model.PaymentOutEntity;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface PaymentOutMapper extends RequestMapper<PaymentOutEntity, PaymentOutRequest> {
    PaymentOutResponse toResponse(PaymentOutEntity entity, List<PaidDocumentResponse> paidDocuments);
}
