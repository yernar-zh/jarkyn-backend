package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.config.Api;
import kz.jarkyn.backend.model.common.api.MessageApi;
import kz.jarkyn.backend.model.group.api.GroupDetailResponse;
import kz.jarkyn.backend.model.group.api.GroupRequest;
import kz.jarkyn.backend.model.group.api.GroupResponse;
import kz.jarkyn.backend.service.GroupService;
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
    public GroupDetailResponse create(@RequestBody GroupRequest request) {
        return groupService.createApi(request);
    }

    @PutMapping("{id}")
    public GroupDetailResponse edit(@PathVariable UUID id, @RequestBody GroupRequest request) {
        return groupService.editApi(id, request);
    }

    @DeleteMapping("{id}")
    public MessageApi delete(@PathVariable UUID id) {
        groupService.delete(id);
        return MessageApi.DELETED;
    }
}