package kz.jarkyn.backend.party.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.party.model.dto.CounterpartyListResponse;
import kz.jarkyn.backend.party.model.dto.CounterpartyRequest;
import kz.jarkyn.backend.party.model.dto.CounterpartyResponse;
import kz.jarkyn.backend.party.service.CounterpartyService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Api.Counterparty.PATH)
public class CounterpartyController {
    private final CounterpartyService counterpartyService;

    public CounterpartyController(
            CounterpartyService counterpartyService
    ) {
        this.counterpartyService = counterpartyService;
    }

    @GetMapping("{id}")
    public CounterpartyResponse detail(@PathVariable UUID id) {
        return counterpartyService.findApiById(id);
    }

    @GetMapping
    public PageResponse<CounterpartyListResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return counterpartyService.findApiByFilter(QueryParams.of(allParams));
    }

    @PostMapping
    public CounterpartyResponse create(@RequestBody CounterpartyRequest request) {
        UUID id = counterpartyService.create(request);
        return counterpartyService.findApiById(id);
    }

    @PutMapping("{id}")
    public CounterpartyResponse edit(@PathVariable UUID id, @RequestBody CounterpartyRequest request) {
        counterpartyService.edit(id, request);
        return counterpartyService.findApiById(id);
    }
}