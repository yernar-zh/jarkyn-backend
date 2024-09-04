package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.model.good.api.GroupListApi;
import kz.jarkyn.backend.service.GroupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("groups")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<GroupListApi> list() {
        return groupService.findApiBy();
    }

    @PostMapping
    public List<GroupListApi> create() {
        return groupService.createApi();
    }
}