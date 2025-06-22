package kz.jarkyn.backend.document.payment.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutListResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutRequest;
import kz.jarkyn.backend.document.payment.model.dto.PaymentOutResponse;
import kz.jarkyn.backend.document.payment.service.PaymentOutService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(Api.PaymentOut.PATH)
public class PaymentOutController {
    private final PaymentOutService paymentOutService;

    public PaymentOutController(PaymentOutService paymentOutService) {
        this.paymentOutService = paymentOutService;
    }


    @GetMapping("{id}")
    public PaymentOutResponse detail(@PathVariable UUID id) {
        return paymentOutService.findApiById(id);
    }


    @GetMapping
    public PageResponse<PaymentOutListResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return paymentOutService.findApiByFilter(QueryParams.of(allParams));
    }


    @PostMapping
    public PaymentOutResponse create(@RequestBody PaymentOutRequest request) {
        UUID id = paymentOutService.createApi(request);
        return paymentOutService.findApiById(id);
    }

    @PutMapping("{id}")
    public PaymentOutResponse edit(@PathVariable UUID id, @RequestBody PaymentOutRequest request) {
        paymentOutService.editApi(id, request);
        return paymentOutService.findApiById(id);
    }

    @PutMapping("{id}/commit")
    public PaymentOutResponse commit(@PathVariable UUID id) {
        paymentOutService.commit(id);
        return paymentOutService.findApiById(id);
    }

    @PutMapping("{id}/undoCommit")
    public PaymentOutResponse undoCommit(@PathVariable UUID id) {
        paymentOutService.undoCommit(id);
        return paymentOutService.findApiById(id);
    }

    @DeleteMapping("{id}")
    public PaymentOutResponse delete(@PathVariable UUID id) {
        paymentOutService.delete(id);
        return paymentOutService.findApiById(id);
    }
}