package ru.practicum.shareitserver.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User save(@RequestBody @Valid User user) {
        log.info("UserController: create is called");
        user = userService.save(user);
        log.info("UserController: user created successfully");
        return user;
    }

    @GetMapping("{userId}")
    public User findById(@PathVariable long userId) {
        log.info("UserController: getById is called");
        User user = userService.findById(userId);
        log.info("UserController: user with id {} successfully received", userId);
        return user;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("UserController: getAll is called");
        List<User> users = userService.findAll();
        log.info("UserController: all users successfully received");
        return users;
    }

    @PatchMapping("{userId}")
    public User update(@PathVariable long userId, @Valid @RequestBody UserDto userDto) {
        log.info("UserController: update is called");
        User user = userService.update(userId, userDto);
        log.info("UserController: updated successfully");
        return user;
    }

    @DeleteMapping("{userId}")
    public void deleteById(@PathVariable long userId) {
        log.info("UserController: deleteById is called");
        userService.deleteById(userId);
        log.info("UserController: deleted by id {} successfully", userId);
    }
}