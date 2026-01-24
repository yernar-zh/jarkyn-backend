package kz.jarkyn.backend.warehouse.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.MessageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.warehouse.model.dto.GroupDetailResponse;
import kz.jarkyn.backend.warehouse.model.dto.GroupRequest;
import kz.jarkyn.backend.warehouse.model.dto.GroupResponse;
import kz.jarkyn.backend.warehouse.service.GroupService;
import org.springframework.util.MultiValueMap;
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

    @GetMapping("tree")
    public List<GroupResponse> tree(@RequestParam MultiValueMap<String, String> allParams) {
        return groupService.findApiTree(QueryParams.ofMulty(allParams));
    }

    @GetMapping("{id}")
    public GroupDetailResponse detail(@PathVariable UUID id) {
        return groupService.findApiById(id);
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
    public MessageResponse delete(@PathVariable UUID id) {
        groupService.delete(id);
        return MessageResponse.DELETED;
    }
}