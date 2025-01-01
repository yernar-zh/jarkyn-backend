package kz.jarkyn.backend.document.payment.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInListResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInRequest;
import kz.jarkyn.backend.document.payment.model.dto.PaymentInResponse;
import kz.jarkyn.backend.document.payment.service.PaymentInService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(Api.PaymentIn.PATH)
public class PaymentInController {
    private final PaymentInService paymentInService;

    public PaymentInController(PaymentInService paymentInService) {
        this.paymentInService = paymentInService;
    }


    @GetMapping("{id}")
    public PaymentInResponse detail(@PathVariable UUID id) {
        return paymentInService.findApiById(id);
    }


    @GetMapping
    public PageResponse<PaymentInListResponse> list(@RequestParam Map<String, String> allParams) {
        return paymentInService.findApiByFilter(QueryParams.of(allParams));
    }


    @PostMapping
    public PaymentInResponse create(@RequestBody PaymentInRequest request) {
        UUID id = paymentInService.createApi(request);
        return paymentInService.findApiById(id);
    }

    @PutMapping("{id}")
    public PaymentInResponse edit(@PathVariable UUID id, @RequestBody PaymentInRequest request) {
        paymentInService.editApi(id, request);
        return paymentInService.findApiById(id);
    }

    @PutMapping("{id}/commit")
    public PaymentInResponse commit(@PathVariable UUID id) {
        paymentInService.commit(id);
        return paymentInService.findApiById(id);
    }

    @PutMapping("{id}/undoCommit")
    public PaymentInResponse undoCommit(@PathVariable UUID id) {
        paymentInService.undoCommit(id);
        return paymentInService.findApiById(id);
    }
}