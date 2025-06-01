package kz.jarkyn.backend.global.service;


import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.global.mapper.ItemOfExpenditureMapper;
import kz.jarkyn.backend.global.model.ItemOfExpenditureEntity;
import kz.jarkyn.backend.global.model.ItemOfExpenditureEntity_;
import kz.jarkyn.backend.global.model.dto.ItemOfExpenditureResponse;
import kz.jarkyn.backend.global.repository.ItemOfExpenditureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ItemOfExpenditureService {
    private final ItemOfExpenditureRepository itemOfExpenditureRepository;
    private final ItemOfExpenditureMapper itemOfExpenditureMapper;
    private final SearchFactory searchFactory;

    public ItemOfExpenditureService(
            ItemOfExpenditureRepository itemOfExpenditureRepository,
            ItemOfExpenditureMapper itemOfExpenditureMapper, SearchFactory searchFactory) {
        this.itemOfExpenditureRepository = itemOfExpenditureRepository;
        this.itemOfExpenditureMapper = itemOfExpenditureMapper;
        this.searchFactory = searchFactory;
    }

    @Transactional(readOnly = true)
    public ItemOfExpenditureResponse findApiById(UUID id) {
        ItemOfExpenditureEntity itemOfExpenditure = itemOfExpenditureRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return itemOfExpenditureMapper.toResponse(itemOfExpenditure);
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemOfExpenditureResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<ItemOfExpenditureEntity> attributes = CriteriaAttributes.<ItemOfExpenditureEntity>builder()
                .add("id", (root) -> root.get(ItemOfExpenditureEntity_.id))
                .add("name", (root) -> root.get(ItemOfExpenditureEntity_.name))
                .add("code", (root) -> root.get(ItemOfExpenditureEntity_.code))
                .add("archived", (root) -> root.get(ItemOfExpenditureEntity_.archived))
                .build();
        Search<ItemOfExpenditureResponse> search = searchFactory.createCriteriaSearch(
                ItemOfExpenditureResponse.class, List.of("name"),
                ItemOfExpenditureEntity.class, attributes);
        return search.getResult(queryParams);
    }

}
