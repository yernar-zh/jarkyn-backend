package kz.jarkyn.backend.document.expense.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseResponse;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseListResponse;
import kz.jarkyn.backend.document.expense.model.dto.ExpenseRequest;
import kz.jarkyn.backend.document.expense.service.ExpenseService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Api.Expense.PATH)
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }


    @GetMapping("{id}")
    public ExpenseResponse detail(@PathVariable UUID id) {
        return expenseService.findApiById(id);
    }


    @GetMapping
    public PageResponse<ExpenseListResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return expenseService.findApiByFilter(QueryParams.ofMulty(allParams));
    }


    @PostMapping
    public ExpenseResponse create(@RequestBody ExpenseRequest request) {
        UUID id = expenseService.createApi(request);
        return expenseService.findApiById(id);
    }

    @PutMapping("{id}")
    public ExpenseResponse edit(@PathVariable UUID id, @RequestBody ExpenseRequest request) {
        expenseService.editApi(id, request);
        return expenseService.findApiById(id);
    }

    @PutMapping("{id}/commit")
    public ExpenseResponse commit(@PathVariable UUID id) {
        expenseService.commit(id);
        return expenseService.findApiById(id);
    }

    @PutMapping("{id}/undoCommit")
    public ExpenseResponse undoCommit(@PathVariable UUID id) {
        expenseService.undoCommit(id);
        return expenseService.findApiById(id);
    }

    @DeleteMapping("{id}")
    public ExpenseResponse delete(@PathVariable UUID id) {
        expenseService.delete(id);
        return expenseService.findApiById(id);
    }
}