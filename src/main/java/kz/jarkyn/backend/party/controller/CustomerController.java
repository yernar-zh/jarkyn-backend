package kz.jarkyn.backend.party.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.party.model.dto.CustomerListResponse;
import kz.jarkyn.backend.party.model.dto.CustomerRequest;
import kz.jarkyn.backend.party.model.dto.CustomerResponse;
import kz.jarkyn.backend.party.service.CustomerService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(Api.Customer.PATH)
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(
            CustomerService customerService
    ) {
        this.customerService = customerService;
    }

    @GetMapping("{id}")
    public CustomerResponse detail(@PathVariable UUID id) {
        return customerService.findApiById(id);
    }

    @GetMapping
    public PageResponse<CustomerListResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return customerService.findApiByFilter(QueryParams.of(allParams));
    }

    @PostMapping
    public CustomerResponse create(@RequestBody CustomerRequest request) {
        UUID id = customerService.createApi(request);
        return customerService.findApiById(id);
    }

    @PutMapping("{id}")
    public CustomerResponse edit(@PathVariable UUID id, @RequestBody CustomerRequest request) {
        customerService.editApi(id, request);
        return customerService.findApiById(id);
    }
}