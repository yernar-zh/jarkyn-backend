package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.config.Api;
import kz.jarkyn.backend.model.good.api.*;
import kz.jarkyn.backend.service.TransportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Api.Transport.PATH)
public class TransportController {
    private final TransportService transportService;

    public TransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    @GetMapping
    public List<TransportListApi> list() {
        return transportService.findApiBy();
    }

    @PostMapping
    public TransportDetailApi create(@RequestBody TransportCreateApi createApi) {
        return transportService.createApi(createApi);
    }

    @PutMapping("{id}")
    public TransportDetailApi edit(@PathVariable UUID id, @RequestBody TransportEditApi editApi) {
        return transportService.editApi(id, editApi);
    }
}