package kz.jarkyn.backend.good.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.good.model.dto.AttributeEditRequest;
import kz.jarkyn.backend.good.model.dto.AttributeRequest;
import kz.jarkyn.backend.good.model.dto.AttributeResponse;
import kz.jarkyn.backend.core.model.dto.MessageResponse;
import kz.jarkyn.backend.good.service.AttributeService;
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
    public AttributeResponse create(@RequestBody AttributeRequest request) {
        return attributeService.createApi(request);
    }

    @PutMapping("{id}")
    public AttributeResponse edit(@PathVariable UUID id, @RequestBody AttributeRequest request) {
        return attributeService.editApi(id, request);
    }

    @DeleteMapping("{id}")
    public MessageResponse delete(@PathVariable UUID id) {
        attributeService.delete(id);
        return MessageResponse.DELETED;
    }
}