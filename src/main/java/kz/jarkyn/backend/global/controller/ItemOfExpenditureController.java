package kz.jarkyn.backend.global.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.global.model.dto.ItemOfExpenditureResponse;
import kz.jarkyn.backend.global.service.ItemOfExpenditureService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Api.ItemOfExpenditure.PATH)
public class ItemOfExpenditureController {

    private final ItemOfExpenditureService itemOfExpenditureService;

    public ItemOfExpenditureController(ItemOfExpenditureService itemOfExpenditureService) {
        this.itemOfExpenditureService = itemOfExpenditureService;
    }

    @GetMapping("{id}")
    public ItemOfExpenditureResponse detail(@PathVariable UUID id) {
        return itemOfExpenditureService.findApiById(id);
    }

    @GetMapping
    public PageResponse<ItemOfExpenditureResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return itemOfExpenditureService.findApiByFilter(QueryParams.of(allParams));
    }

}
