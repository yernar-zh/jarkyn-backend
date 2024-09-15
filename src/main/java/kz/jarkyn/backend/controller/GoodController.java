package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.config.Api;
import kz.jarkyn.backend.model.good.api.*;
import kz.jarkyn.backend.service.GoodService;
import kz.jarkyn.backend.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Api.Group.PATH)
public class GoodController {
    private final GoodService goodService;

    public GoodController(GoodService goodService) {
        this.goodService = goodService;
    }

    @GetMapping("{id}")
    public GoodDetailApi detail(@PathVariable UUID id) {
        return goodService.findApiById(id);
    }

    @GetMapping
    public List<GoodListApi> list() {
        return goodService.findApiByFilter();
    }

    @PostMapping
    public GoodDetailApi create(@RequestBody GoodCreateApi createApi) {
        return goodService.createApi(createApi);
    }

    @PutMapping("{id}")
    public GoodDetailApi edit(@PathVariable UUID id, @RequestBody GoodEditApi editApi) {
        return goodService.editApi(id, editApi);
    }
}