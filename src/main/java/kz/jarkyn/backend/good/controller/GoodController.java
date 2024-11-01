package kz.jarkyn.backend.good.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.good.model.dto.GoodRequest;
import kz.jarkyn.backend.good.model.dto.GoodResponse;
import kz.jarkyn.backend.core.model.dto.ValueDto;
import kz.jarkyn.backend.good.model.filter.GoodApiFilter;
import kz.jarkyn.backend.good.service.GoodService;
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
    public List<GoodResponse> list(@ModelAttribute GoodApiFilter filter) {
        return goodService.findApiByFilter(filter);
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