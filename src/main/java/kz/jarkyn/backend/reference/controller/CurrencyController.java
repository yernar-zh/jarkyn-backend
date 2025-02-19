package kz.jarkyn.backend.reference.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.reference.model.dto.CurrencyResponse;
import kz.jarkyn.backend.reference.service.CurrencyService;
import org.springframework.web.bind.annotation.*;

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
    public PageResponse<CurrencyResponse> list(@RequestParam Map<String, String> allParams) {
        return currencyService.findApiByFilter(QueryParams.of(allParams));
    }
}