package kz.jarkyn.backend.document.change.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.document.change.model.ChargeDocumentEntity;
import kz.jarkyn.backend.document.change.model.dto.ChargeDocumentRequest;
import kz.jarkyn.backend.document.change.model.dto.ChargeDocumentResponse;
import org.mapstruct.Mapper;

@Mapper(uses = EntityMapper.class)
public interface ChargeDocumentMapper
        extends RequestResponseMapper<ChargeDocumentEntity, ChargeDocumentRequest, ChargeDocumentResponse> {
}
