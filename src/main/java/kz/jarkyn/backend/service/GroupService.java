package kz.jarkyn.backend.service;


import kz.jarkyn.backend.exception.ApiValidationException;
import kz.jarkyn.backend.exception.DataValidationException;
import kz.jarkyn.backend.exception.ExceptionUtils;
import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.model.group.GroupEntity;
import kz.jarkyn.backend.model.group.api.GroupDetailApi;
import kz.jarkyn.backend.model.group.api.GroupEditApi;
import kz.jarkyn.backend.model.group.api.GroupListApi;
import kz.jarkyn.backend.model.group.api.GroupCreateApi;
import kz.jarkyn.backend.repository.GroupRepository;
import kz.jarkyn.backend.service.mapper.GroupMapper;
import kz.jarkyn.backend.service.utils.EntityDivider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
    }

    @Transactional(readOnly = true)
    public GroupDetailApi findApiById(UUID id) {
        GroupEntity entity = groupRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<GroupEntity> children = groupRepository.findByParent(entity);
        children.sort(Comparator.comparing(GroupEntity::getPosition).thenComparing(GroupEntity::getCreatedAt));
        return groupMapper.toDetailApi(entity, children);
    }

    @Transactional(readOnly = true)
    public List<GroupListApi> findApiAll() {
        List<GroupEntity> entities = groupRepository.findAll();
        // Create a map of parent groups to their children using groupingBy
        Map<GroupEntity, List<GroupEntity>> childrenMap = entities.stream()
                .filter(group -> group.getParent() != null) // Filter out groups without parents
                .collect(Collectors.groupingBy(GroupEntity::getParent,
                        Collectors.collectingAndThen(
                                Collectors.toList(), list -> {
                                    list.sort(Comparator.comparing(GroupEntity::getPosition)
                                            .thenComparing(GroupEntity::getCreatedAt));
                                    return list;
                                }
                        )
                ));
        // Find all head groups (groups without a parent) using a separate stream operation
        List<GroupEntity> heads = entities.stream().filter(group -> group.getParent() == null).toList();
        // Convert head groups into API model objects, using the childrenMap for hierarchy
        return heads.stream().map(head -> groupMapper.toListApi(head, childrenMap)).collect(Collectors.toList());
    }

    @Transactional
    public GroupDetailApi createApi(GroupCreateApi createApi) {
        GroupEntity entity = groupMapper.toEntity(createApi);
        entity.setPosition(1000);
        groupRepository.save(entity);
        return findApiById(entity.getId());
    }

    @Transactional
    public GroupDetailApi editApi(UUID id, GroupEditApi editApi) {
        GroupEntity entity = groupRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        groupMapper.editEntity(entity, editApi);
        for (GroupEntity parent = entity.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.equals(entity)) {
                throw new DataValidationException("EXIST_PARENT_LOOP", "You are trying to move a group into itself");
            }
        }
        groupRepository.save(entity);
        EntityDivider<GroupEntity, IdApi> divider = new EntityDivider<>(
                groupRepository.findByParent(entity), editApi.getChildren());
        if (!divider.newReceived().isEmpty() || !divider.skippedCurrent().isEmpty()) {
            throw new ApiValidationException("children list have to be same");
        }
        for (EntityDivider<GroupEntity, IdApi>.Entry entry : divider.edited()) {
            GroupEntity childrenEntity = entry.getCurrent();
            childrenEntity.setPosition(entry.getReceivedPosition());
            groupRepository.save(childrenEntity);
        }
        return findApiById(entity.getId());
    }
}
