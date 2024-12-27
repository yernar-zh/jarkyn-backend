package kz.jarkyn.backend.good.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.good.model.dto.AttributeGroupDetailResponse;
import kz.jarkyn.backend.good.model.dto.AttributeGroupResponse;
import kz.jarkyn.backend.good.model.dto.AttributeGroupRequest;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.MessageResponse;
import kz.jarkyn.backend.good.service.AttributeGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Api.AttributeGroup.PATH)
public class AttributeGroupController {
    private final AttributeGroupService attributeGroupService;

    public AttributeGroupController(AttributeGroupService attributeGroupService) {
        this.attributeGroupService = attributeGroupService;
    }

    @GetMapping("{id}")
    public AttributeGroupDetailResponse detail(@PathVariable UUID id) {
        return attributeGroupService.findApiById(id);
    }

    @GetMapping
    public List<AttributeGroupResponse> list() {
        return attributeGroupService.findApiAll();
    }

    @PutMapping
    public List<AttributeGroupResponse> move(@RequestBody List<IdDto> apiList) {
        return attributeGroupService.moveApi(apiList);
    }

    @PostMapping
    public AttributeGroupDetailResponse create(@RequestBody AttributeGroupRequest request) {
        return attributeGroupService.createApi(request);
    }

    @PutMapping("{id}")
    public AttributeGroupDetailResponse edit(@PathVariable UUID id, @RequestBody AttributeGroupRequest request) {
        return attributeGroupService.editApi(id, request);
    }

    @DeleteMapping("{id}")
    public MessageResponse delete(@PathVariable UUID id) {
        attributeGroupService.delete(id);
        return MessageResponse.DELETED;
    }
}