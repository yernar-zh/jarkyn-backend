package kz.jarkyn.backend.warehouse.service;


import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.warehouse.mapper.AttributeGroupMapper;
import kz.jarkyn.backend.warehouse.model.dto.AttributeGroupRequest;
import kz.jarkyn.backend.warehouse.model.dto.AttributeGroupResponse;
import kz.jarkyn.backend.warehouse.model.AttributeEntity;
import kz.jarkyn.backend.warehouse.model.AttributeGroupEntity;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.warehouse.repository.AttributeGroupRepository;
import kz.jarkyn.backend.core.utils.EntityDivider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AttributeGroupService {
    private final AttributeGroupRepository attributeGroupRepository;
    private final AttributeService attributeService;
    private final AttributeGroupMapper attributeGroupMapper;
    private final SearchFactory searchFactory;

    public AttributeGroupService(
            AttributeGroupRepository attributeGroupRepository,
            AttributeService attributeService,
            AttributeGroupMapper attributeGroupMapper, SearchFactory searchFactory) {
        this.attributeGroupRepository = attributeGroupRepository;
        this.attributeService = attributeService;
        this.attributeGroupMapper = attributeGroupMapper;
        this.searchFactory = searchFactory;
    }

    @Transactional(readOnly = true)
    public AttributeGroupResponse findApiById(UUID id) {
        AttributeGroupEntity entity = attributeGroupRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        List<AttributeEntity> attributes = attributeService.findByGroup(entity);
        return attributeGroupMapper.toResponse(entity, attributes);
    }

    @Transactional(readOnly = true)
    public PageResponse<AttributeGroupResponse> findApiByFilter(QueryParams queryParams) {
        Search<AttributeGroupResponse> search = searchFactory.createListSearch(
                AttributeGroupResponse.class, List.of("name"), () ->
                        attributeGroupRepository.findAll().stream().map(attributeGroup -> {
                            List<AttributeEntity> attributes = attributeService.findByGroup(attributeGroup);
                            return attributeGroupMapper.toResponse(attributeGroup, attributes);
                        }).toList());
        return search.getResult(queryParams);
    }

    @Transactional
    public PageResponse<AttributeGroupResponse> moveApi(List<IdDto> apiList) {
        EntityDivider<AttributeGroupEntity, IdDto> divider = new EntityDivider<>(
                attributeGroupRepository.findAll(), apiList
        );
        if (!divider.newReceived().isEmpty() || !divider.skippedCurrent().isEmpty()) {
            throw new ApiValidationException("list should contain only and all current groups");
        }
        for (EntityDivider<AttributeGroupEntity, IdDto>.Entry entry : divider.edited()) {
            AttributeGroupEntity entity = entry.getCurrent();
            entity.setPosition(entry.getReceivedPosition());
            attributeGroupRepository.save(entity);
        }
        return findApiByFilter(QueryParams.of());
    }

    @Transactional
    public AttributeGroupResponse createApi(AttributeGroupRequest request) {
        AttributeGroupEntity entity = attributeGroupRepository.save(attributeGroupMapper.toEntity(request));
        entity.setPosition(1000);
        return findApiById(entity.getId());
    }

    @Transactional
    public AttributeGroupResponse editApi(UUID id, AttributeGroupRequest request) {
        AttributeGroupEntity entity = attributeGroupRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        attributeGroupMapper.editEntity(entity, request);
        attributeGroupRepository.save(entity);
        EntityDivider<AttributeEntity, IdDto> divider = new EntityDivider<>(
                attributeService.findByGroup(entity), request.getAttributes());
        if (!divider.newReceived().isEmpty() || !divider.skippedCurrent().isEmpty()) {
            throw new ApiValidationException("children list have to be same, only change positions");
        }
        for (EntityDivider<AttributeEntity, IdDto>.Entry entry : divider.edited()) {
            AttributeEntity attributeEntity = entry.getCurrent();
            attributeEntity.setPosition(entry.getReceivedPosition());
        }
        return findApiById(entity.getId());
    }

    @Transactional
    public void delete(UUID id) {
        AttributeGroupEntity entity = attributeGroupRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        if (!attributeService.findByGroup(entity).isEmpty()) {
            ExceptionUtils.throwRelationException();
        }
        attributeGroupRepository.delete(entity);
    }
}
