package kz.jarkyn.backend.document.expense.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentResponse;
import kz.jarkyn.backend.document.expense.model.ExpenseEntity;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseRequest;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface ExpenseMapper extends RequestMapper<ExpenseEntity, ExpenseRequest> {
    ExpenseResponse toResponse(ExpenseEntity entity, List<BindDocumentResponse> bindDocuments);
}
