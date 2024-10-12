package kz.jarkyn.backend.service;


import kz.jarkyn.backend.exception.ApiValidationException;
import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.attribute.AttributeGroupEntity;
import kz.jarkyn.backend.model.attribute.api.*;
import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.model.good.GoodTransportEntity;
import kz.jarkyn.backend.repository.AttributeGroupRepository;
import kz.jarkyn.backend.repository.AttributeRepository;
import kz.jarkyn.backend.service.mapper.AttributeMapper;
import kz.jarkyn.backend.service.utils.EntityDivider;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttributeGroupService {
    private final AttributeGroupRepository attributeGroupRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeMapper attributeMapper;

    public AttributeGroupService(
            AttributeGroupRepository attributeGroupRepository,
            AttributeMapper attributeMapper
    ) {
        this.attributeGroupRepository = attributeGroupRepository;
        this.attributeMapper = attributeMapper;
    }

    @Transactional(readOnly = true)
    public List<AttributeGroupListApi> findApiBy() {
        List<AttributeGroupEntity> entities = attributeGroupRepository.findAll();
        entities.sort(Comparator.comparing(AttributeGroupEntity::getPosition)
                .thenComparing(AttributeGroupEntity::getCreatedAt));
        return attributeMapper.toListApi(entities);
    }

    @Transactional
    public List<AttributeGroupListApi> moveApi(List<IdApi> apiList) {
        EntityDivider<AttributeGroupEntity, IdApi, UUID> divider = new EntityDivider<>(
                attributeGroupRepository.findAll(), AttributeGroupEntity::getId,
                apiList, IdApi::getId
        );
        if (!divider.newReceived().isEmpty() || divider.skippedCurrent().isEmpty()) {
            throw new ApiValidationException("list should contain only and all current groups");
        }
        for (Pair<AttributeGroupEntity, Pair<IdApi, Integer>> attributeGroupEntityPairPair : divider.edited()) {
            AttributeGroupEntity entity = attributeGroupEntityPairPair.getFirst();
            entity.setPosition(attributeGroupEntityPairPair.getSecond().getSecond());
            attributeGroupRepository.save(entity);
        }
        return findApiBy();
    }

    @Transactional
    public AttributeGroupDetailApi createApi(AttributeGroupCreateApi createApi) {
        AttributeGroupEntity entity = attributeMapper.toEntity(createApi);
        entity.setPosition(1000);
        return attributeMapper.toDetailApi(attributeGroupRepository.save(entity));
    }

    @Transactional
    public AttributeGroupDetailApi editApi(UUID id, AttributeGroupEditApi editApi) {
        AttributeGroupEntity entity = attributeGroupRepository.findById(id).orElseThrow();
        EntityDivider<AttributeEntity, AttributeApi, UUID> divider = new EntityDivider<>(
                attributeRepository.findByGroup(entity), AttributeEntity::getId,
                editApi.getAttributes(), AttributeApi::getId
        );
        for (Pair<AttributeApi, Integer> pair : divider.newReceived()) {
            attributeMapper.toEntity(pair.getFirst())
        }
        if (!divider.newReceived().isEmpty() || divider.skippedCurrent().isEmpty()) {
            throw new ApiValidationException("list should contain only and all current attribute");
        }
        attributeMapper.editEntity(entity, editApi);
        return transportMapper.toDetailApi(transportRepository.save(entity));
    }
}
