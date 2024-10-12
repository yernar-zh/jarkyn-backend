package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.config.Api;
import kz.jarkyn.backend.model.attribute.api.AttributeGroupCreateApi;
import kz.jarkyn.backend.model.attribute.api.AttributeGroupDetailApi;
import kz.jarkyn.backend.model.attribute.api.AttributeGroupEditApi;
import kz.jarkyn.backend.model.attribute.api.AttributeGroupListApi;
import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.service.AttributeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Api.Transport.PATH)
public class AttributeController {
    private final AttributeService attributeService;

    public AttributeController(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @GetMapping("{id}")
    public AttributeGroupDetailApi detail(@PathVariable UUID id) {
        return attributeService.findApiById(id);
    }

    @GetMapping
    public List<AttributeGroupListApi> list() {
        return attributeService.findApiAll();
    }

    @PutMapping
    public List<AttributeGroupListApi> move(@RequestBody List<IdApi> apiList) {
        return attributeService.moveApi(apiList);
    }

    @PostMapping
    public AttributeGroupDetailApi create(@RequestBody AttributeGroupCreateApi createApi) {
        return attributeService.createApi(createApi);
    }

    @PutMapping("{id}")
    public AttributeGroupDetailApi edit(@PathVariable UUID id, @RequestBody AttributeGroupEditApi editApi) {
        return attributeService.editApi(id, editApi);
    }
}