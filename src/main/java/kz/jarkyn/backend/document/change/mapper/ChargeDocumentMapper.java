package kz.jarkyn.backend.document.change.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.document.change.model.ChargeDocumentEntity;
import kz.jarkyn.backend.document.change.model.dto.ChargeDocumentRequest;
import kz.jarkyn.backend.document.change.model.dto.ChargeDocumentResponse;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface ChargeDocumentMapper
        extends RequestResponseMapper<ChargeDocumentEntity, ChargeDocumentRequest, ChargeDocumentResponse> {
}
