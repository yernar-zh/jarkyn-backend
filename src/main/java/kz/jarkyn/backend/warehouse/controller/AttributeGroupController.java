package kz.jarkyn.backend.warehouse.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.warehouse.model.dto.AttributeGroupResponse;
import kz.jarkyn.backend.warehouse.model.dto.AttributeGroupRequest;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.MessageResponse;
import kz.jarkyn.backend.warehouse.service.AttributeGroupService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(Api.AttributeGroup.PATH)
public class AttributeGroupController {
    private final AttributeGroupService attributeGroupService;

    public AttributeGroupController(AttributeGroupService attributeGroupService) {
        this.attributeGroupService = attributeGroupService;
    }

    @GetMapping("{id}")
    public AttributeGroupResponse detail(@PathVariable UUID id) {
        return attributeGroupService.findApiById(id);
    }

    @GetMapping
    public PageResponse<AttributeGroupResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return attributeGroupService.findApiByFilter(QueryParams.of(allParams));
    }

    @PutMapping
    public PageResponse<AttributeGroupResponse> move(@RequestBody List<IdDto> apiList) {
        return attributeGroupService.moveApi(apiList);
    }

    @PostMapping
    public AttributeGroupResponse create(@RequestBody AttributeGroupRequest request) {
        return attributeGroupService.createApi(request);
    }

    @PutMapping("{id}")
    public AttributeGroupResponse edit(@PathVariable UUID id, @RequestBody AttributeGroupRequest request) {
        return attributeGroupService.editApi(id, request);
    }

    @DeleteMapping("{id}")
    public MessageResponse delete(@PathVariable UUID id) {
        attributeGroupService.delete(id);
        return MessageResponse.DELETED;
    }
}