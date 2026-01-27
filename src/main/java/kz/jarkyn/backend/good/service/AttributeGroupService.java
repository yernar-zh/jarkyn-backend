package kz.jarkyn.backend.good.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.good.mapper.AttributeGroupMapper;
import kz.jarkyn.backend.good.model.AttributeGroupEntity_;
import kz.jarkyn.backend.good.model.dto.AttributeGroupRequest;
import kz.jarkyn.backend.good.model.dto.AttributeGroupResponse;
import kz.jarkyn.backend.good.model.AttributeGroupEntity;
import kz.jarkyn.backend.good.repository.AttributeGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AttributeGroupService {
    private final AttributeGroupRepository attributeGroupRepository;
    private final AttributeService attributeService;
    private final AttributeGroupMapper attributeGroupMapper;
    private final SearchFactory searchFactory;
    private final AuditService auditService;

    public AttributeGroupService(
            AttributeGroupRepository attributeGroupRepository,
            AttributeService attributeService,
            AttributeGroupMapper attributeGroupMapper, SearchFactory searchFactory, AuditService auditService) {
        this.attributeGroupRepository = attributeGroupRepository;
        this.attributeService = attributeService;
        this.attributeGroupMapper = attributeGroupMapper;
        this.searchFactory = searchFactory;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public AttributeGroupResponse findApiById(UUID id) {
        AttributeGroupEntity attribute = attributeGroupRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        return attributeGroupMapper.toResponse(attribute);
    }

    @Transactional(readOnly = true)
    public PageResponse<AttributeGroupResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<AttributeGroupEntity> attributes = CriteriaAttributes.<AttributeGroupEntity>builder()
                .add("id", (root) -> root.get(AttributeGroupEntity_.id))
                .add("name", (root) -> root.get(AttributeGroupEntity_.name))
                .add("archived", (root) -> root.get(AttributeGroupEntity_.archived))
                .build();
        Search<AttributeGroupResponse> search = searchFactory.createCriteriaSearch(
                AttributeGroupResponse.class, List.of("name"), QueryParams.Sort.NAME_ASC,
                AttributeGroupEntity.class, attributes);
        return search.getResult(queryParams);
    }

    @Transactional
    public AttributeGroupResponse createApi(AttributeGroupRequest request) {
        AttributeGroupEntity attributeGroup = attributeGroupRepository.save(attributeGroupMapper.toEntity(request));
        auditService.saveEntity(attributeGroup);
        return findApiById(attributeGroup.getId());
    }

    @Transactional
    public AttributeGroupResponse editApi(UUID id, AttributeGroupRequest request) {
        AttributeGroupEntity attributeGroup = attributeGroupRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        attributeGroupMapper.editEntity(attributeGroup, request);
        auditService.saveEntity(attributeGroup);
        attributeGroupRepository.save(attributeGroup);
        return findApiById(attributeGroup.getId());
    }

    @Transactional
    public void delete(UUID id) {
        AttributeGroupEntity entity = attributeGroupRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        if (!attributeService.findByGroup(entity).isEmpty()) {
            ExceptionUtils.throwRelationDeleteException();
        }
        attributeGroupRepository.delete(entity);
    }
}
