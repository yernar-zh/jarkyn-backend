package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.config.Api;
import kz.jarkyn.backend.model.attribute.api.*;
import kz.jarkyn.backend.model.common.api.MessageApi;
import kz.jarkyn.backend.service.AttributeService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Api.Attribute.PATH)
public class AttributeController {
    private final AttributeService attributeService;

    public AttributeController(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @GetMapping("{id}")
    public AttributeResponse detail(@PathVariable UUID id) {
        return attributeService.findApiById(id);
    }

    @PostMapping
    public AttributeResponse create(@RequestBody AttributeRequest createApi) {
        return attributeService.createApi(createApi);
    }

    @PutMapping("{id}")
    public AttributeResponse edit(@PathVariable UUID id, @RequestBody AttributeEditRequest editApi) {
        return attributeService.editApi(id, editApi);
    }

    @DeleteMapping("{id}")
    public MessageApi delete(@PathVariable UUID id) {
        attributeService.delete(id);
        return MessageApi.DELETED;
    }
}