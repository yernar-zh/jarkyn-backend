package kz.jarkyn.backend.good.service;


import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.good.mapper.AttributeGroupMapper;
import kz.jarkyn.backend.good.model.dto.AttributeGroupDetailResponse;
import kz.jarkyn.backend.good.model.dto.AttributeGroupRequest;
import kz.jarkyn.backend.good.model.dto.AttributeGroupResponse;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.good.model.AttributeGroupEntity;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.good.repository.AttributeGroupRepository;
import kz.jarkyn.backend.core.utils.EntityDivider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AttributeGroupService {
    private final AttributeGroupRepository attributeGroupRepository;
    private final AttributeService attributeService;
    private final AttributeGroupMapper attributeGroupMapper;

    public AttributeGroupService(
            AttributeGroupRepository attributeGroupRepository,
            AttributeService attributeService,
            AttributeGroupMapper attributeGroupMapper
    ) {
        this.attributeGroupRepository = attributeGroupRepository;
        this.attributeService = attributeService;
        this.attributeGroupMapper = attributeGroupMapper;
    }

    @Transactional(readOnly = true)
    public AttributeGroupDetailResponse findApiById(UUID id) {
        AttributeGroupEntity entity = attributeGroupRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        List<AttributeEntity> attributes = attributeService.findByGroup(entity);
        return attributeGroupMapper.toResponse(entity, attributes);
    }

    @Transactional(readOnly = true)
    public List<AttributeGroupResponse> findApiAll() {
        List<AttributeGroupEntity> entities = attributeGroupRepository.findAll();
        entities.sort(Comparator.comparing(AttributeGroupEntity::getPosition));
        return attributeGroupMapper.toResponse(entities);
    }

    @Transactional
    public List<AttributeGroupResponse> moveApi(List<IdDto> apiList) {
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
        return findApiAll();
    }

    @Transactional
    public AttributeGroupDetailResponse createApi(AttributeGroupRequest request) {
        AttributeGroupEntity entity = attributeGroupRepository.save(attributeGroupMapper.toEntity(request));
        entity.setPosition(1000);
        return findApiById(entity.getId());
    }

    @Transactional
    public AttributeGroupDetailResponse editApi(UUID id, AttributeGroupRequest request) {
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
