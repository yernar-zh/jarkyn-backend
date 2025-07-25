package kz.jarkyn.backend.warehouse.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import kz.jarkyn.backend.warehouse.model.dto.GoodListResponse;
import kz.jarkyn.backend.warehouse.model.dto.GoodRequest;
import kz.jarkyn.backend.warehouse.model.dto.GoodResponse;
import kz.jarkyn.backend.core.model.dto.ValueDto;
import kz.jarkyn.backend.warehouse.service.GoodService;
import kz.jarkyn.backend.warehouse.service.WarehouseService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping(Api.Good.PATH)
public class GoodController {
    private final GoodService goodService;

    public GoodController(
            GoodService goodService,
            EntityMapper entityMapper) {
        this.goodService = goodService;
    }

    @GetMapping("{id}")
    public GoodResponse detail(@PathVariable UUID id) {
        return goodService.findApiById(id);
    }

    @GetMapping
    public PageResponse<GoodListResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        List<UUID> warehouseIds = Optional.ofNullable(allParams.remove("$warehouse.id"))
                .orElse(List.of()).stream().map(UUID::fromString).toList();
        Instant moment = Optional.ofNullable(allParams.remove("$moment"))
                .orElse(List.of()).stream().map(Instant::parse).findFirst().orElse(null);
        return goodService.findApiByFilter(QueryParams.of(allParams), warehouseIds, moment);
    }

    @PostMapping
    public GoodResponse create(@RequestBody GoodRequest request) {
        return goodService.createApi(request);
    }

    @PutMapping("{id}")
    public GoodResponse edit(@PathVariable UUID id, @RequestBody GoodRequest request) {
        return goodService.editApi(id, request);
    }

    @PutMapping("{id}/archive")
    public GoodResponse archive(@PathVariable UUID id, @RequestBody ValueDto<Boolean> valueApi) {
        return goodService.archive(id, valueApi.getValue());
    }
}