package kz.jarkyn.backend.party.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.MessageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.party.model.dto.AccountRequest;
import kz.jarkyn.backend.party.model.dto.AccountResponse;
import kz.jarkyn.backend.party.service.AccountService;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(Api.Account.PATH)
public class AccountController {
    private final AccountService accountService;

    public AccountController(
            AccountService accountService
    ) {
        this.accountService = accountService;
    }

    @GetMapping("{id}")
    public AccountResponse detail(@PathVariable UUID id) {
        return accountService.findApiById(id);
    }

    @GetMapping
    public PageResponse<AccountResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return accountService.findApiByFilter(QueryParams.of(allParams));
    }


    @PostMapping
    public AccountResponse create(@RequestBody AccountRequest request) {
        UUID id = accountService.createApi(request);
        return accountService.findApiById(id);
    }

    @PutMapping("{id}")
    public AccountResponse edit(@PathVariable UUID id, @RequestBody AccountRequest request) {
        accountService.editApi(id, request);
        return accountService.findApiById(id);
    }

    @PutMapping("{id}/archive")
    public AccountResponse archive(@PathVariable UUID id) {
        return accountService.archive(id);
    }

    @PutMapping("{id}/unarchive")
    public AccountResponse unarchive(@PathVariable UUID id) {
        return accountService.unarchive(id);
    }

    @DeleteMapping("{id}")
    public MessageResponse delete(@PathVariable UUID id) {
        accountService.delete(id);
        return MessageResponse.DELETED;
    }
}