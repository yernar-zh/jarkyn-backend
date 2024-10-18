package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.config.Api;
import kz.jarkyn.backend.model.attribute.api.AttributeGroupResponse;
import kz.jarkyn.backend.model.attribute.api.AttributeGroupRequest;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.api.MessageApi;
import kz.jarkyn.backend.service.AttributeGroupService;
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
    public AttributeGroupResponse detail(@PathVariable UUID id) {
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
    public AttributeGroupResponse create(@RequestBody AttributeGroupRequest createApi) {
        return attributeGroupService.createApi(createApi);
    }

    @PutMapping("{id}")
    public AttributeGroupResponse edit(@PathVariable UUID id, @RequestBody AttributeGroupRequest editApi) {
        return attributeGroupService.editApi(id, editApi);
    }

    @DeleteMapping("{id}")
    public MessageApi delete(@PathVariable UUID id) {
        attributeGroupService.delete(id);
        return MessageApi.DELETED;
    }
}