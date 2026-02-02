package kz.jarkyn.backend.good.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.exception.DataValidationException;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.core.sorts.EntitySorts;
import kz.jarkyn.backend.good.model.GroupEntity;
import kz.jarkyn.backend.good.model.dto.GroupDetailResponse;
import kz.jarkyn.backend.good.model.dto.GroupRequest;
import kz.jarkyn.backend.good.model.dto.GroupResponse;
import kz.jarkyn.backend.good.model.dto.ImmutableGroupResponse;
import kz.jarkyn.backend.good.repository.GoodRepository;
import kz.jarkyn.backend.good.repository.GroupRepository;
import kz.jarkyn.backend.good.mapper.GroupMapper;
import kz.jarkyn.backend.core.utils.EntityDivider;
import kz.jarkyn.backend.good.specifications.GroupSpecifications;
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
    private final SearchFactory searchFactory;

    public GroupService(
            GroupRepository groupRepository,
            GoodRepository goodRepository,
            GroupMapper groupMapper,
            AuditService auditService,
            SearchFactory searchFactory) {
        this.groupRepository = groupRepository;
        this.goodRepository = goodRepository;
        this.groupMapper = groupMapper;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
    }

    @Transactional(readOnly = true)
    public GroupDetailResponse findApiById(UUID id) {
        GroupEntity entity = groupRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<GroupEntity> children = groupRepository.findByParent(entity);
        children.sort(Comparator.comparing(GroupEntity::getPosition));
        return groupMapper.toDetailApi(entity, children);
    }

    @Transactional(readOnly = true)
    public GroupDetailResponse findFirstRoot() {
        GroupEntity entity = groupRepository.findOne(
                GroupSpecifications.root(),
                EntitySorts.byCreatedAsc()
        ).orElseThrow(ExceptionUtils.entityNotFound());
        return findApiById(entity.getId());
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> findApiTree(QueryParams queryParams) {
        List<GroupEntity> entities = groupRepository.findAll().stream()
                .sorted(Comparator.comparing(GroupEntity::getPosition)).toList();
        Map<GroupEntity, List<GroupEntity>> childrenMap = entities.stream()
                .filter(group -> group.getParent() != null)
                .collect(Collectors.groupingBy(GroupEntity::getParent,
                        Collectors.collectingAndThen(Collectors.toList(), list ->
                                list.stream().sorted(Comparator.comparing(GroupEntity::getPosition)).toList())));
        List<GroupResponse> tree = entities.stream().filter(group -> group.getParent() == null)
                .map(head -> groupMapper.toListApi(head, childrenMap)).toList();

        Search<GroupResponse> search = searchFactory.createListSearch(
                GroupResponse.class, List.of("name", "searchKeywords"), QueryParams.Sort.NAME_ASC, () -> {
                    List<GroupResponse> flattens = new ArrayList<>();
                    makeFlatten(tree, flattens);
                    return flattens;
                });
        Set<UUID> filteredIds = search.getResult(queryParams).getRow()
                .stream().map(IdDto::getId).collect(Collectors.toSet());

        return filter(tree, filteredIds);
    }

    private List<GroupResponse> filter(List<GroupResponse> trees, Set<UUID> filteredIds) {
        List<GroupResponse> result = new ArrayList<>();
        for (GroupResponse tree : trees) {
            List<GroupResponse> children = filter(tree.getChildren(), filteredIds);
            if (children.isEmpty() && !filteredIds.contains(tree.getId())) continue;
            result.add(ImmutableGroupResponse.builder().from(tree).children(children).build());
        }
        return result;
    }

    private void makeFlatten(List<GroupResponse> trees, List<GroupResponse> flattens) {
        for (GroupResponse tree : trees) {
            flattens.add(tree);
            makeFlatten(tree.getChildren(), flattens);
        }
    }

    @Transactional
    public GroupDetailResponse createApi(GroupRequest request) {
        GroupEntity entity = groupMapper.toEntity(request);
        entity.setPosition(1000);
        groupRepository.save(entity);
        auditService.saveEntity(entity);
        return findApiById(entity.getId());
    }

    @Transactional
    public GroupDetailResponse editApi(UUID id, GroupRequest request) {
        GroupEntity entity = groupRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        groupMapper.editEntity(entity, request);
        for (GroupEntity parent = entity.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.equals(entity)) {
                throw new DataValidationException("Вы пытаетесь переместить группу внутрь самой себя");
            }
        }
        groupRepository.save(entity);
        auditService.saveEntity(entity);
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
            ExceptionUtils.throwRelationDeleteException();
        }
        if (!goodRepository.findByGroup(good).isEmpty()) {
            ExceptionUtils.throwRelationDeleteException();
        }
        groupRepository.delete(good);
    }
}
