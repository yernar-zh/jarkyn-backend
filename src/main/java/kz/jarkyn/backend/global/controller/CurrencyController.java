package kz.jarkyn.backend.global.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.global.model.dto.CurrencyResponse;
import kz.jarkyn.backend.global.service.CurrencyService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(Api.Currency.PATH)
public class CurrencyController {
    private final CurrencyService currencyService;

    public CurrencyController(
            CurrencyService currencyService
    ) {
        this.currencyService = currencyService;
    }

    @GetMapping("{id}")
    public CurrencyResponse detail(@PathVariable UUID id) {
        return currencyService.findApiById(id);
    }

    @GetMapping
    public PageResponse<CurrencyResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return currencyService.findApiByFilter(QueryParams.of(allParams));
    }
}