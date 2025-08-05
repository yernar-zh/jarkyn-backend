
package kz.jarkyn.backend.global.service;


import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.global.mapper.CoverageMapper;
import kz.jarkyn.backend.global.model.CoverageEntity;
import kz.jarkyn.backend.global.model.CoverageEntity_;
import kz.jarkyn.backend.global.repository.CoverageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CoverageService {
    private final CoverageRepository coverageRepository;
    private final CoverageMapper coverageMapper;
    private final SearchFactory searchFactory;

    public CoverageService(
            CoverageRepository coverageRepository,
            CoverageMapper coverageMapper,
            SearchFactory searchFactory
    ) {
        this.coverageRepository = coverageRepository;
        this.coverageMapper = coverageMapper;
        this.searchFactory = searchFactory;
    }

    @Transactional(readOnly = true)
    public EnumTypeResponse findApiById(UUID id) {
        CoverageEntity coverage = coverageRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return coverageMapper.toResponse(coverage);
    }

    @Transactional(readOnly = true)
    public PageResponse<EnumTypeResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<CoverageEntity> attributes = CriteriaAttributes.<CoverageEntity>builder()
                .add("id", (root) -> root.get(CoverageEntity_.id))
                .add("name", (root) -> root.get(CoverageEntity_.name))
                .add("code", (root) -> root.get(CoverageEntity_.code))
                .add("archived", (root) -> root.get(CoverageEntity_.archived))
                .build();
        Search<EnumTypeResponse> search = searchFactory.createCriteriaSearch(
                EnumTypeResponse.class, List.of("name"),
                CoverageEntity.class, attributes);
        return search.getResult(queryParams);
    }
}
