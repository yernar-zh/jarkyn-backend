package kz.jarkyn.backend.document.payment.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentRequest;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import org.mapstruct.Mapper;

@Mapper(uses = EntityMapper.class)
public abstract class PaidDocumentMapper
        extends RequestResponseMapper<PaidDocumentEntity, PaidDocumentRequest, PaidDocumentResponse> {
}
