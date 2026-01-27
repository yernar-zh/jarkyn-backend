package kz.jarkyn.backend.good.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.good.model.*;
import kz.jarkyn.backend.good.model.dto.AttributeRequest;
import kz.jarkyn.backend.good.model.dto.AttributeResponse;
import kz.jarkyn.backend.good.repository.AttributeRepository;
import kz.jarkyn.backend.good.repository.GoodRepository;
import kz.jarkyn.backend.good.mapper.AttributeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class AttributeService {
    private final AttributeRepository attributeRepository;
    private final GoodRepository goodRepository;
    private final AttributeMapper attributeMapper;
    private final AuditService auditService;
    private final SearchFactory searchFactory;

    public AttributeService(
            AttributeRepository attributeRepository,
            GoodRepository goodRepository,
            AttributeMapper attributeMapper,
            AuditService auditService, SearchFactory searchFactory) {
        this.attributeRepository = attributeRepository;
        this.goodRepository = goodRepository;
        this.attributeMapper = attributeMapper;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
    }

    @Transactional(readOnly = true)
    public AttributeResponse findApiById(UUID id) {
        AttributeEntity attribute = attributeRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return attributeMapper.toResponse(attribute);
    }

    @Transactional(readOnly = true)
    public PageResponse<AttributeResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<AttributeEntity> attributes = CriteriaAttributes.<AttributeEntity>builder()
                .add("id", (root) -> root.get(AttributeEntity_.id))
                .add("name", (root) -> root.get(AttributeEntity_.name))
                .add("archived", (root) -> root.get(AttributeEntity_.archived))
                .add("searchKeywords", (root) -> root.get(AttributeEntity_.searchKeywords))
                .addReference("group", (root) -> root.get(AttributeEntity_.group))
                .build();
        Search<AttributeResponse> search = searchFactory.createCriteriaSearch(
                AttributeResponse.class, List.of("name", "searchKeywords"), QueryParams.Sort.NAME_ASC,
                AttributeEntity.class, attributes);
        return search.getResult(queryParams);
    }

    @Transactional
    public AttributeResponse createApi(AttributeRequest request) {
        AttributeEntity attribute = attributeRepository.save(attributeMapper.toEntity(request));
        auditService.saveEntity(attribute);
        return findApiById(attribute.getId());
    }

    @Transactional
    public AttributeResponse editApi(UUID id, AttributeRequest request) {
        AttributeEntity attribute = attributeRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        attributeMapper.editEntity(attribute, request);
        auditService.saveEntity(attribute);
        return findApiById(attribute.getId());
    }

    @Transactional
    public void delete(UUID id) {
        AttributeEntity attribute = attributeRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (!goodRepository.findByAttribute(attribute).isEmpty()) {
            ExceptionUtils.throwRelationDeleteException();
        }
        attributeRepository.delete(attribute);
    }

    public List<AttributeEntity> findByGroup(AttributeGroupEntity entity) {
        return attributeRepository.findByGroup(entity).stream()
                .sorted(Comparator.comparing(AttributeEntity::getName))
                .toList();
    }
}
