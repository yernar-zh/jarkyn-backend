package kz.jarkyn.backend.good.service;


import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.good.model.AttributeGroupEntity;
import kz.jarkyn.backend.good.model.dto.AttributeEditRequest;
import kz.jarkyn.backend.good.model.dto.AttributeRequest;
import kz.jarkyn.backend.good.model.dto.AttributeResponse;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.good.repository.AttributeRepository;
import kz.jarkyn.backend.good.repository.GoodRepository;
import kz.jarkyn.backend.good.mapper.AttributeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttributeService {
    private final AttributeRepository attributeRepository;
    private final GoodRepository goodRepository;
    private final AttributeMapper attributeMapper;

    public AttributeService(
            AttributeRepository attributeRepository,
            GoodRepository goodRepository,
            AttributeMapper attributeMapper
    ) {
        this.attributeRepository = attributeRepository;
        this.goodRepository = goodRepository;
        this.attributeMapper = attributeMapper;
    }

    @Transactional(readOnly = true)
    public AttributeResponse findApiById(UUID id) {
        AttributeEntity entity = attributeRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return attributeMapper.toResponse(entity);
    }

    @Transactional
    public AttributeResponse createApi(AttributeRequest request) {
        AttributeEntity entity = attributeRepository.save(attributeMapper.toEntity(request));
        entity.setPosition(1000);
        return findApiById(entity.getId());
    }

    @Transactional
    public AttributeResponse editApi(UUID id, AttributeRequest request) {
        AttributeEntity entity = attributeRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        ExceptionUtils.requireEqualsApi(entity.getGroup().getId(), request.getGroup().getId(), "group");
        attributeMapper.editEntity(entity, request);
        return findApiById(entity.getId());
    }

    @Transactional
    public void delete(UUID id) {
        AttributeEntity attribute = attributeRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (!goodRepository.findByAttribute(attribute).isEmpty()) {
            ExceptionUtils.throwRelationException();
        }
        attributeRepository.delete(attribute);
    }

    public List<AttributeEntity> findByGroup(AttributeGroupEntity entity) {
        return attributeRepository.findByGroup(entity).stream()
                .sorted(Comparator.comparing(AttributeEntity::getPosition))
                .toList();
    }
}
