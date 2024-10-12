package kz.jarkyn.backend.service;


import kz.jarkyn.backend.exception.ExceptionUtils;
import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.attribute.api.*;
import kz.jarkyn.backend.repository.AttributeRepository;
import kz.jarkyn.backend.service.mapper.AttributeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AttributeService {
    private final AttributeRepository attributeRepository;
    private final AttributeMapper attributeMapper;

    public AttributeService(
            AttributeRepository attributeRepository,
            AttributeMapper attributeMapper
    ) {
        this.attributeRepository = attributeRepository;
        this.attributeMapper = attributeMapper;
    }

    @Transactional(readOnly = true)
    public AttributeDetailApi findApiById(UUID id) {
        AttributeEntity entity = attributeRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        return attributeMapper.toDetailApi(entity);
    }

    @Transactional
    public AttributeDetailApi createApi(AttributeCreateApi createApi) {
        AttributeEntity entity = attributeMapper.toEntity(createApi);
        entity.setPosition(1000);
        attributeRepository.save(entity);
        return findApiById(entity.getId());
    }

    @Transactional
    public AttributeDetailApi editApi(UUID id, AttributeEditApi editApi) {
        AttributeEntity entity = attributeRepository.findById(id).orElseThrow();
        attributeMapper.editEntity(entity, editApi);
        attributeRepository.save(entity);
        return findApiById(entity.getId());
    }

    @Transactional
    public void delete(UUID id) {
        AttributeEntity entity = attributeRepository.findById(id).orElseThrow();
        attributeRepository.delete(entity);
    }
}
