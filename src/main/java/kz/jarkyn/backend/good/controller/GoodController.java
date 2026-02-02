package kz.jarkyn.backend.good.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.MessageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.good.model.dto.GoodListResponse;
import kz.jarkyn.backend.good.model.dto.GoodRequest;
import kz.jarkyn.backend.good.model.dto.GoodResponse;
import kz.jarkyn.backend.global.model.dto.BulkResponse;
import kz.jarkyn.backend.global.service.BulkService;
import kz.jarkyn.backend.global.model.dto.BulkUpdateRequest;
import kz.jarkyn.backend.good.service.GoodService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Api.Good.PATH)
public class GoodController {
    private final GoodService goodService;
    private final BulkService bulkService;

    public GoodController(GoodService goodService, BulkService bulkService) {
        this.goodService = goodService;
        this.bulkService = bulkService;
    }

    @GetMapping("{id}")
    public GoodResponse detail(@PathVariable UUID id) {
        return goodService.findApiById(id);
    }

    @GetMapping
    public PageResponse<GoodListResponse> list(@RequestParam MultiValueMap<String, String> queryParams) {
        return list(new HashMap<>(queryParams));
    }

    @PostMapping("search")
    public PageResponse<GoodListResponse> search(@RequestBody Map<String, Object> bodyParams) {
        Map<String, List<String>> allParams = bodyParams.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                            if (entry.getValue() instanceof List<?> list) {
                                return list.stream().map(Object::toString).toList();
                            } else {
                                return List.of(entry.getValue().toString());
                            }
                        }
                ));
        return list(allParams);
    }

    private PageResponse<GoodListResponse> list(Map<String, List<String>> allParams) {
        List<UUID> warehouseIds = Optional.ofNullable(allParams.remove("$warehouse.id"))
                .orElse(List.of()).stream().map(UUID::fromString).toList();

        Instant moment = Optional.ofNullable(allParams.remove("$moment"))
                .orElse(List.of()).stream().map(Instant::parse).findFirst().orElse(null);

        return goodService.findApiByFilter(QueryParams.ofMulty(allParams), warehouseIds, moment);
    }


    @PostMapping()
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

    @PostMapping("bulk")
    public List<BulkResponse<GoodResponse>> bulkCreate(@RequestBody List<GoodRequest> request) {
        return bulkService.bulkCreate(request, goodService::createApi);
    }

    @PutMapping("bulk")
    public List<BulkResponse<GoodResponse>> bulkUpdate(@RequestBody List<BulkUpdateRequest<GoodRequest>> request) {
        return bulkService.bulkUpdate(request, goodService::editApi);
    }
}
