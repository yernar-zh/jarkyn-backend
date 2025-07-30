package kz.jarkyn.backend.audit.controller;

import kz.jarkyn.backend.audit.model.dto.MainEntityChangeResponse;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.controller.Api;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Api.History.PATH)
public class HistoryController {
    private final AuditService auditService;

    public HistoryController(AuditService auditService) {
        this.auditService = auditService;
    }


    @GetMapping("{entityId}")
    public List<MainEntityChangeResponse> detail(@PathVariable UUID entityId) {
        return auditService.findChanges(entityId);
    }
}