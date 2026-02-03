package kz.jarkyn.backend.party.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.MessageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.party.model.dto.CounterpartyListResponse;
import kz.jarkyn.backend.party.model.dto.CounterpartyRequest;
import kz.jarkyn.backend.party.model.dto.CounterpartyResponse;
import kz.jarkyn.backend.global.model.dto.BulkResponse;
import kz.jarkyn.backend.global.model.dto.BulkUpdateRequest;
import kz.jarkyn.backend.global.service.BulkService;
import kz.jarkyn.backend.party.service.CounterpartyService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Api.Counterparty.PATH)
public class CounterpartyController {
    private final CounterpartyService counterpartyService;
    private final BulkService bulkService;

    public CounterpartyController(
            CounterpartyService counterpartyService,
            BulkService bulkService
    ) {
        this.counterpartyService = counterpartyService;
        this.bulkService = bulkService;
    }

    @GetMapping("{id}")
    public CounterpartyResponse detail(@PathVariable UUID id) {
        return counterpartyService.findApiById(id);
    }

    @GetMapping
    public PageResponse<CounterpartyListResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return counterpartyService.findApiByFilter(QueryParams.ofMulty(allParams));
    }

    @PostMapping
    public CounterpartyResponse create(@RequestBody CounterpartyRequest request) {
        UUID id = counterpartyService.createApi(request);
        return counterpartyService.findApiById(id);
    }

    @PutMapping("{id}")
    public CounterpartyResponse edit(@PathVariable UUID id, @RequestBody CounterpartyRequest request) {
        counterpartyService.editApi(id, request);
        return counterpartyService.findApiById(id);
    }


    @PutMapping("{id}/archive")
    public CounterpartyResponse archive(@PathVariable UUID id) {
        return counterpartyService.archive(id);
    }

    @PutMapping("{id}/unarchive")
    public CounterpartyResponse unarchive(@PathVariable UUID id) {
        return counterpartyService.unarchive(id);
    }

    @DeleteMapping("{id}")
    public MessageResponse delete(@PathVariable UUID id) {
        counterpartyService.delete(id);
        return MessageResponse.DELETED;
    }

    @PostMapping("bulk")
    public List<BulkResponse<CounterpartyResponse>> bulkCreate(@RequestBody List<CounterpartyRequest> request) {
        return bulkService.bulkCreate(request, req -> counterpartyService.findApiById(counterpartyService.createApi(req)));
    }

    @PutMapping("bulk")
    public List<BulkResponse<CounterpartyResponse>> bulkUpdate(
            @RequestBody List<BulkUpdateRequest<CounterpartyRequest>> request) {
        return bulkService.bulkUpdate(request, (id, req) -> {
            counterpartyService.editApi(id, req);
            return counterpartyService.findApiById(id);
        });
    }
}
