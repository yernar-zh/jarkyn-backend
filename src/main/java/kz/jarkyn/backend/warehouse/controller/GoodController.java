package kz.jarkyn.backend.warehouse.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.MessageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.warehouse.model.dto.GoodListResponse;
import kz.jarkyn.backend.warehouse.model.dto.GoodRequest;
import kz.jarkyn.backend.warehouse.model.dto.GoodResponse;
import kz.jarkyn.backend.warehouse.service.GoodService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

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

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public PageResponse<GoodListResponse> list(
            @RequestParam MultiValueMap<String, String> queryParams,
            @RequestBody Map<String, Object> bodyParams) {

        // Combine parameters or decide which one to use
        Map<String, List<String>> allParams = new HashMap<>(queryParams);
        bodyParams.forEach((key, value) -> {
            if (value instanceof List<?> list) {
                allParams.put(key, list.stream().map(Object::toString).toList());
            } else if (value != null) {
                allParams.put(key, List.of(value.toString()));
            }
        });

        List<UUID> warehouseIds = Optional.ofNullable(allParams.remove("$warehouse.id"))
                .orElse(List.of()).stream().map(UUID::fromString).toList();

        Instant moment = Optional.ofNullable(allParams.remove("$moment"))
                .orElse(List.of()).stream().map(Instant::parse).findFirst().orElse(null);

        return goodService.findApiByFilter(QueryParams.ofMulty(allParams), warehouseIds, moment);
    }


    @PostMapping("create")
    public GoodResponse create(@RequestBody GoodRequest request) {
        return goodService.createApi(request);
    }

    @PutMapping("{id}")
    public GoodResponse edit(@PathVariable UUID id, @RequestBody GoodRequest request) {
        return goodService.editApi(id, request);
    }

    @PutMapping("{id}/archive")
    public GoodResponse archive(@PathVariable UUID id) {
        return goodService.archive(id);
    }

    @PutMapping("{id}/unarchive")
    public GoodResponse unarchive(@PathVariable UUID id) {
        return goodService.unarchive(id);
    }

    @DeleteMapping("{id}")
    public MessageResponse delete(@PathVariable UUID id) {
        goodService.delete(id);
        return MessageResponse.DELETED;
    }
}