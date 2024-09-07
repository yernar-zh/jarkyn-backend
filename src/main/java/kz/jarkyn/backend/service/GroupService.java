package kz.jarkyn.backend.service;


import kz.jarkyn.backend.model.good.GroupEntity;
import kz.jarkyn.backend.model.good.api.GroupDetailApi;
import kz.jarkyn.backend.model.good.api.GroupEditApi;
import kz.jarkyn.backend.model.good.api.GroupListApi;
import kz.jarkyn.backend.model.good.api.GroupCreateApi;
import kz.jarkyn.backend.repository.GroupRepository;
import kz.jarkyn.backend.service.mapper.GroupMapper;
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
    public List<GroupListApi> findApiBy() {
        List<GroupEntity> entities = groupRepository.findAll();
        // Create a map of parent groups to their children using groupingBy
        Map<GroupEntity, List<GroupEntity>> childrenMap = entities.stream().filter(group -> group.getParent() != null) // Filter out groups without parents
                .collect(Collectors.groupingBy(GroupEntity::getParent));
        // Find all head groups (groups without a parent) using a separate stream operation
        List<GroupEntity> heads = entities.stream().filter(group -> group.getParent() == null).toList();
        // Convert head groups into API model objects, using the childrenMap for hierarchy
        return heads.stream().map(head -> groupMapper.toListApi(head, childrenMap)).collect(Collectors.toList());
    }

    @Transactional
    public GroupDetailApi createApi(GroupCreateApi createApi) {
        GroupEntity entity = groupMapper.toEntity(createApi);
        return groupMapper.toDetailApi(groupRepository.save(entity));
    }

    @Transactional
    public GroupDetailApi editApi(UUID id, GroupEditApi editApi) {
        GroupEntity entity = groupRepository.findById(id).orElseThrow();
        groupMapper.editEntity(entity, editApi);
        return groupMapper.toDetailApi(groupRepository.save(entity));
    }
}
