package kz.jarkyn.backend.counterparty.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.counterparty.model.dto.AccountRequest;
import kz.jarkyn.backend.counterparty.model.dto.AccountResponse;
import kz.jarkyn.backend.counterparty.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public List<AccountResponse> list() {
        return accountService.findApiAll();
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
}