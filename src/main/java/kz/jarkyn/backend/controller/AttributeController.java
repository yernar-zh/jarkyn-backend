package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.config.Api;
import kz.jarkyn.backend.model.attribute.api.*;
import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.model.common.api.MessageApi;
import kz.jarkyn.backend.service.AttributeGroupService;
import kz.jarkyn.backend.service.AttributeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Api.Attribute.PATH)
public class AttributeController {
    private final AttributeService attributeService;

    public AttributeController(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @GetMapping("{id}")
    public AttributeDetailApi detail(@PathVariable UUID id) {
        return attributeService.findApiById(id);
    }

    @PostMapping
    public AttributeDetailApi create(@RequestBody AttributeCreateApi createApi) {
        return attributeService.createApi(createApi);
    }

    @PutMapping("{id}")
    public AttributeDetailApi edit(@PathVariable UUID id, @RequestBody AttributeEditApi editApi) {
        return attributeService.editApi(id, editApi);
    }

    @DeleteMapping("{id}")
    public MessageApi delete(@PathVariable UUID id) {
        attributeService.delete(id);
        return MessageApi.DELETED;
    }
}