package kz.jarkyn.backend.warehouse.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.warehouse.model.dto.AttributeGroupResponse;
import kz.jarkyn.backend.warehouse.model.dto.AttributeRequest;
import kz.jarkyn.backend.warehouse.model.dto.AttributeResponse;
import kz.jarkyn.backend.core.model.dto.MessageResponse;
import kz.jarkyn.backend.warehouse.service.AttributeService;
import org.springframework.util.MultiValueMap;
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

    @GetMapping
    public PageResponse<AttributeResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return attributeService.findApiByFilter(QueryParams.of(allParams));
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