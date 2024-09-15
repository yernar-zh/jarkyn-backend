package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.config.Api;
import kz.jarkyn.backend.model.good.api.GroupCreateApi;
import kz.jarkyn.backend.model.good.api.GroupDetailApi;
import kz.jarkyn.backend.model.good.api.GroupEditApi;
import kz.jarkyn.backend.model.good.api.GroupListApi;
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
    public GroupDetailApi detail(@PathVariable UUID id) {
        return groupService.findApiById(id);
    }

    @GetMapping
    public List<GroupListApi> list() {
        return groupService.findApiAll();
    }

    @PostMapping
    public GroupDetailApi create(@RequestBody GroupCreateApi createApi) {
        return groupService.createApi(createApi);
    }

    @PutMapping("{id}")
    public GroupDetailApi edit(@PathVariable UUID id, @RequestBody GroupEditApi editApi) {
        return groupService.editApi(id, editApi);
    }
}