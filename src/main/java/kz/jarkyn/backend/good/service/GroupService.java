package kz.jarkyn.backend.good.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.exception.DataValidationException;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.good.model.GroupEntity;
import kz.jarkyn.backend.good.model.dto.GroupDetailResponse;
import kz.jarkyn.backend.good.model.dto.GroupRequest;
import kz.jarkyn.backend.good.model.dto.GroupResponse;
import kz.jarkyn.backend.good.repository.GoodRepository;
import kz.jarkyn.backend.good.repository.GroupRepository;
import kz.jarkyn.backend.good.mapper.GroupMapper;
import kz.jarkyn.backend.core.utils.EntityDivider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GoodRepository goodRepository;
    private final GroupMapper groupMapper;
    private final AuditService auditService;

    public GroupService(
            GroupRepository groupRepository,
            GoodRepository goodRepository,
            GroupMapper groupMapper,
            AuditService auditService
    ) {
        this.groupRepository = groupRepository;
        this.goodRepository = goodRepository;
        this.groupMapper = groupMapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public GroupDetailResponse findApiById(UUID id) {
        GroupEntity entity = groupRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<GroupEntity> children = groupRepository.findByParent(entity);
        children.sort(Comparator.comparing(GroupEntity::getPosition));
        return groupMapper.toDetailApi(entity, children);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> findApiAll() {
        List<GroupEntity> entities = groupRepository.findAll().stream()
                .sorted(Comparator.comparing(GroupEntity::getPosition)).toList();
        Map<GroupEntity, List<GroupEntity>> childrenMap = entities.stream()
                .filter(group -> group.getParent() != null)
                .collect(Collectors.groupingBy(GroupEntity::getParent,
                        Collectors.collectingAndThen(
                                Collectors.toList(), list -> {
                                    list.sort(Comparator.comparing(GroupEntity::getPosition));
                                    return list;
                                }
                        )
                ));
        List<GroupEntity> roots = entities.stream()
                .sorted(Comparator.comparing(GroupEntity::getPosition))
                .filter(group -> group.getParent() == null).toList();
        return roots.stream().map(head -> groupMapper.toListApi(head, childrenMap)).collect(Collectors.toList());
    }

    @Transactional
    public GroupDetailResponse createApi(GroupRequest request) {
        GroupEntity entity = groupMapper.toEntity(request);
        entity.setPosition(1000);
        groupRepository.save(entity);
        auditService.saveChanges(entity);
        return findApiById(entity.getId());
    }

    @Transactional
    public GroupDetailResponse editApi(UUID id, GroupRequest request) {
        GroupEntity entity = groupRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        groupMapper.editEntity(entity, request);
        for (GroupEntity parent = entity.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.equals(entity)) {
                throw new DataValidationException("EXIST_PARENT_LOOP", "You are trying to move a group into itself");
            }
        }
        groupRepository.save(entity);
        EntityDivider<GroupEntity, IdDto> divider = new EntityDivider<>(
                groupRepository.findByParent(entity), request.getChildren());
        if (!divider.newReceived().isEmpty() || !divider.skippedCurrent().isEmpty()) {
            throw new ApiValidationException("children list have to be same");
        }
        for (EntityDivider<GroupEntity, IdDto>.Entry entry : divider.edited()) {
            GroupEntity childrenEntity = entry.getCurrent();
            childrenEntity.setPosition(entry.getReceivedPosition());
            groupRepository.save(childrenEntity);
        }
        return findApiById(entity.getId());
    }

    @Transactional
    public void delete(UUID id) {
        GroupEntity good = groupRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        if (!groupRepository.findByParent(good).isEmpty()) {
            ExceptionUtils.throwRelationException();
        }
        if (!goodRepository.findByGroup(good).isEmpty()) {
            ExceptionUtils.throwRelationException();
        }
        groupRepository.delete(good);
    }
}
