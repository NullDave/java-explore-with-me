package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserAdminController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll(@RequestParam(name = "ids", required = false) List<Long> ids,
                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all users by userIds={}, from={}, size={}", ids, from, size);
        return userService.getAllByAdmin(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@RequestBody @Valid UserDto userDto) {
        log.info("Add user={}", userDto);
        return userService.addByAdmin(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        log.info("Delete user by id={}", userId);
        userService.deleteByAdmin(userId);
    }
}
