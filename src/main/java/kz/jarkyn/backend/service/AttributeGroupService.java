package kz.jarkyn.backend.service;


import kz.jarkyn.backend.exception.ApiValidationException;
import kz.jarkyn.backend.exception.ExceptionUtils;
import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.attribute.AttributeGroupEntity;
import kz.jarkyn.backend.model.attribute.api.*;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.repository.AttributeGroupRepository;
import kz.jarkyn.backend.repository.AttributeRepository;
import kz.jarkyn.backend.service.mapper.AttributeMapper;
import kz.jarkyn.backend.utils.EntityDivider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AttributeGroupService {
    private final AttributeGroupRepository attributeGroupRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeMapper attributeMapper;

    public AttributeGroupService(
            AttributeGroupRepository attributeGroupRepository, AttributeRepository attributeRepository,
            AttributeMapper attributeMapper
    ) {
        this.attributeGroupRepository = attributeGroupRepository;
        this.attributeRepository = attributeRepository;
        this.attributeMapper = attributeMapper;
    }

    @Transactional(readOnly = true)
    public AttributeGroupResponse findApiById(UUID id) {
        AttributeGroupEntity entity = attributeGroupRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        List<AttributeEntity> attributes = attributeRepository.findByGroup(entity);
        attributes.sort(Comparator.comparing(AttributeEntity::getPosition).thenComparing(AttributeEntity::getCreatedAt));
        return attributeMapper.toDetailApi(entity, attributes);
    }

    @Transactional(readOnly = true)
    public List<AttributeGroupResponse> findApiAll() {
        List<AttributeGroupEntity> entities = attributeGroupRepository.findAll();
        entities.sort(Comparator.comparing(AttributeGroupEntity::getPosition)
                .thenComparing(AttributeGroupEntity::getCreatedAt));
        return attributeMapper.toListApi(entities);
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
    public AttributeGroupResponse createApi(AttributeGroupRequest request) {
        AttributeGroupEntity entity = attributeMapper.toEntity(request);
        entity.setPosition(1000);
        attributeGroupRepository.save(entity);
        return findApiById(entity.getId());
    }

    @Transactional
    public AttributeGroupResponse editApi(UUID id, AttributeGroupRequest request) {
        AttributeGroupEntity entity = attributeGroupRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        attributeMapper.editEntity(entity, request);
        attributeGroupRepository.save(entity);
        EntityDivider<AttributeEntity, IdDto> divider = new EntityDivider<>(
                attributeRepository.findByGroup(entity), request.getAttributes());
        if (!divider.newReceived().isEmpty() || !divider.skippedCurrent().isEmpty()) {
            throw new ApiValidationException("children list have to be same");
        }
        for (EntityDivider<AttributeEntity, IdDto>.Entry entry : divider.edited()) {
            AttributeEntity attributeEntity = entry.getCurrent();
            attributeEntity.setPosition(entry.getReceivedPosition());
            attributeRepository.save(attributeEntity);
        }
        return findApiById(entity.getId());
    }

    @Transactional
    public void delete(UUID id) {
        AttributeGroupEntity entity = attributeGroupRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        if (!attributeRepository.findByGroup(entity).isEmpty()) {
            ExceptionUtils.throwRelationException();
        }
        attributeGroupRepository.delete(entity);
    }
}
