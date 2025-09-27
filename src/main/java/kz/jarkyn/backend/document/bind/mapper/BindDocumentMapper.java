package kz.jarkyn.backend.document.bind.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.document.bind.model.BindDocumentEntity;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentRequest;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(config = BaseMapperConfig.class)
public interface BindDocumentMapper extends RequestMapper<BindDocumentEntity, BindDocumentRequest> {
    BindDocumentResponse toResponse(BindDocumentEntity entity, BigDecimal notPaidAmount);
}
