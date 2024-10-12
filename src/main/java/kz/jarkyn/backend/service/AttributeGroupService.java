package kz.jarkyn.backend.service;


import kz.jarkyn.backend.exception.ApiValidationException;
import kz.jarkyn.backend.exception.DataValidationException;
import kz.jarkyn.backend.exception.ExceptionUtils;
import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.attribute.AttributeGroupEntity;
import kz.jarkyn.backend.model.attribute.api.*;
import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.model.common.api.IdNamedApi;
import kz.jarkyn.backend.repository.AttributeGroupRepository;
import kz.jarkyn.backend.repository.AttributeRepository;
import kz.jarkyn.backend.service.mapper.AttributeMapper;
import kz.jarkyn.backend.service.utils.EntityDivider;
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
    public AttributeGroupDetailApi findApiById(UUID id) {
        AttributeGroupEntity entity = attributeGroupRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        List<AttributeEntity> attributes = attributeRepository.findByGroup(entity);
        attributes.sort(Comparator.comparing(AttributeEntity::getPosition).thenComparing(AttributeEntity::getCreatedAt));
        return attributeMapper.toDetailApi(entity, attributes);
    }

    @Transactional(readOnly = true)
    public List<AttributeGroupListApi> findApiAll() {
        List<AttributeGroupEntity> entities = attributeGroupRepository.findAll();
        entities.sort(Comparator.comparing(AttributeGroupEntity::getPosition)
                .thenComparing(AttributeGroupEntity::getCreatedAt));
        return attributeMapper.toListApi(entities);
    }

    @Transactional
    public List<AttributeGroupListApi> moveApi(List<IdApi> apiList) {
        EntityDivider<AttributeGroupEntity, IdApi> divider = new EntityDivider<>(
                attributeGroupRepository.findAll(), apiList
        );
        if (!divider.newReceived().isEmpty() || divider.skippedCurrent().isEmpty()) {
            throw new ApiValidationException("list should contain only and all current groups");
        }
        for (EntityDivider<AttributeGroupEntity, IdApi>.Entry entry : divider.edited()) {
            AttributeGroupEntity entity = entry.getCurrent();
            entity.setPosition(entity.getPosition());
            attributeGroupRepository.save(entity);
        }
        return findApiAll();
    }

    @Transactional
    public AttributeGroupDetailApi createApi(AttributeGroupCreateApi createApi) {
        AttributeGroupEntity entity = attributeMapper.toEntity(createApi);
        entity.setPosition(1000);
        attributeGroupRepository.save(entity);
        return findApiById(entity.getId());
    }

    @Transactional
    public AttributeGroupDetailApi editApi(UUID id, AttributeGroupEditApi editApi) {
        AttributeGroupEntity entity = attributeGroupRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        attributeMapper.editEntity(entity, editApi);
        attributeGroupRepository.save(entity);
        EntityDivider<AttributeEntity, IdApi> divider = new EntityDivider<>(
                attributeRepository.findByGroup(entity), editApi.getAttributes());
        if (!divider.newReceived().isEmpty() || !divider.skippedCurrent().isEmpty()) {
            throw new ApiValidationException("children list have to be same");
        }
        for (EntityDivider<AttributeEntity, IdApi>.Entry entry : divider.edited()) {
            AttributeEntity attributeEntity = entry.getCurrent();
            attributeEntity.setPosition(entity.getPosition());
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
