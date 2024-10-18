package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.config.Api;
import kz.jarkyn.backend.model.common.dto.ValueDto;
import kz.jarkyn.backend.model.good.api.*;
import kz.jarkyn.backend.model.good.apiFilter.GoodApiFilter;
import kz.jarkyn.backend.service.GoodService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Api.Good.PATH)
public class GoodController {
    private final GoodService goodService;

    public GoodController(GoodService goodService) {
        this.goodService = goodService;
    }

    @GetMapping("{id}")
    public GoodResponse detail(@PathVariable UUID id) {
        return goodService.findApiById(id);
    }

    @GetMapping
    public List<GoodResponse> list(GoodApiFilter filter) {
        return goodService.findApiByFilter(filter);
    }

    @PostMapping
    public GoodResponse create(@RequestBody GoodRequest createApi) {
        return goodService.createApi(createApi);
    }

    @PutMapping("{id}")
    public GoodResponse edit(@PathVariable UUID id, @RequestBody GoodRequest editApi) {
        return goodService.editApi(id, editApi);
    }

    @PutMapping("{id}/archive")
    public GoodResponse archive(@PathVariable UUID id, @RequestBody ValueDto<Boolean> valueApi) {
        return goodService.archive(id, valueApi.getValue());
    }
}