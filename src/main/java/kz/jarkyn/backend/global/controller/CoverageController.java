package kz.jarkyn.backend.global.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.global.service.CoverageService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Api.Coverage.PATH)
public class CoverageController {
    private final CoverageService coverageService;

    public CoverageController(
            CoverageService coverageService
    ) {
        this.coverageService = coverageService;
    }

    @GetMapping("{id}")
    public EnumTypeResponse detail(@PathVariable UUID id) {
        return coverageService.findApiById(id);
    }

    @GetMapping
    public PageResponse<EnumTypeResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return coverageService.findApiByFilter(QueryParams.ofMulty(allParams));
    }
}