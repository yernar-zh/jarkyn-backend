package kz.jarkyn.backend.good.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.MessageResponse;
import kz.jarkyn.backend.good.model.dto.GroupDetailResponse;
import kz.jarkyn.backend.good.model.dto.GroupRequest;
import kz.jarkyn.backend.good.model.dto.GroupResponse;
import kz.jarkyn.backend.good.service.GroupService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Api.Group.PATH)
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("{id}")
    public GroupDetailResponse detail(@PathVariable UUID id) {
        return groupService.findApiById(id);
    }

    @GetMapping
    public List<GroupResponse> list() {
        return groupService.findApiAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('GROUP_CREATE')")
    public GroupDetailResponse create(@RequestBody GroupRequest request) {
        return groupService.createApi(request);
    }

    @PutMapping("{id}")
    public GroupDetailResponse edit(@PathVariable UUID id, @RequestBody GroupRequest request) {
        return groupService.editApi(id, request);
    }

    @DeleteMapping("{id}")
    public MessageResponse delete(@PathVariable UUID id) {
        groupService.delete(id);
        return MessageResponse.DELETED;
    }
}