package kz.jarkyn.backend.document.expense.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.document.expense.model.RelatedExpenseEntity;
import kz.jarkyn.backend.document.expense.model.dto.RelatedExpenseRequest;
import kz.jarkyn.backend.document.expense.model.dto.RelatedExpenseResponse;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface RelatedExpenseMapper extends RequestResponseMapper<RelatedExpenseEntity, RelatedExpenseRequest, RelatedExpenseResponse> {
}
