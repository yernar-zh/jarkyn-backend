package kz.jarkyn.backend.user.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.user.service.RoleService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Api.Role.PATH)
public class RoleController {
    private final RoleService roleService;

    public RoleController(
            RoleService roleService
    ) {
        this.roleService = roleService;
    }

    @GetMapping("{id}")
    public EnumTypeResponse detail(@PathVariable UUID id) {
        return roleService.findApiById(id);
    }

    @GetMapping
    public PageResponse<EnumTypeResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return roleService.findApiByFilter(QueryParams.ofMulty(allParams));
    }
}