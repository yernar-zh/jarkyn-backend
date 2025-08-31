package kz.jarkyn.backend.user.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.MessageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.user.model.dto.UserRequest;
import kz.jarkyn.backend.user.model.dto.UserResponse;
import kz.jarkyn.backend.user.service.UserService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Api.User.PATH)
public class UserController {
    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping("{id}")
    public UserResponse detail(@PathVariable UUID id) {
        return userService.findApiById(id);
    }

    @GetMapping
    public PageResponse<UserResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return userService.findApiByFilter(QueryParams.of(allParams));
    }

    @PostMapping
    public UserResponse create(@RequestBody UserRequest request) {
        UUID id = userService.createApi(request);
        return userService.findApiById(id);
    }

    @PutMapping("{id}")
    public UserResponse edit(@PathVariable UUID id, @RequestBody UserRequest request) {
        userService.editApi(id, request);
        return userService.findApiById(id);
    }

    @PutMapping("{id}/archive")
    public UserResponse archive(@PathVariable UUID id) {
        return userService.archive(id);
    }

    @PutMapping("{id}/unarchive")
    public UserResponse unarchive(@PathVariable UUID id) {
        return userService.unarchive(id);
    }

    @DeleteMapping("{id}")
    public MessageResponse delete(@PathVariable UUID id) {
        userService.delete(id);
        return MessageResponse.DELETED;
    }
}