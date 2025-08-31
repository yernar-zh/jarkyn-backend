
package kz.jarkyn.backend.user.service;


import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.user.mapper.RoleMapper;
import kz.jarkyn.backend.user.model.RoleEntity;
import kz.jarkyn.backend.user.model.RoleEntity_;
import kz.jarkyn.backend.user.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final SearchFactory searchFactory;

    public RoleService(
            RoleRepository roleRepository,
            RoleMapper roleMapper,
            SearchFactory searchFactory
    ) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.searchFactory = searchFactory;
    }

    @Transactional(readOnly = true)
    public EnumTypeResponse findApiById(UUID id) {
        RoleEntity role = roleRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return roleMapper.toResponse(role);
    }

    @Transactional(readOnly = true)
    public PageResponse<EnumTypeResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<RoleEntity> attributes = CriteriaAttributes.<RoleEntity>builder()
                .add("id", (root) -> root.get(RoleEntity_.id))
                .add("name", (root) -> root.get(RoleEntity_.name))
                .add("code", (root) -> root.get(RoleEntity_.code))
                .add("archived", (root) -> root.get(RoleEntity_.archived))
                .build();
        Search<EnumTypeResponse> search = searchFactory.createCriteriaSearch(
                EnumTypeResponse.class, QueryParams.NAME_SEARCH, QueryParams.Sort.NAME_ASC,
                RoleEntity.class, attributes);
        return search.getResult(queryParams);
    }
}
