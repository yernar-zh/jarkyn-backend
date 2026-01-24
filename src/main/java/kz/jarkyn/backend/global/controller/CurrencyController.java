package kz.jarkyn.backend.global.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.global.service.CurrencyService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

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
    public EnumTypeResponse detail(@PathVariable UUID id) {
        return currencyService.findApiById(id);
    }

    @GetMapping
    public PageResponse<EnumTypeResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return currencyService.findApiByFilter(QueryParams.ofMulty(allParams));
    }
}