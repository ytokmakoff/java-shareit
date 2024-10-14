package ru.practicum.shareitgateway.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.user.dto.User;
import ru.practicum.shareitgateway.user.dto.UserDto;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid User user) {
        log.info("save user: {}", user);
        return userClient.save(user);
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> findById(@PathVariable long userId) {
        log.info("findById userId={}", userId);
        return userClient.findById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("findAll");
        return userClient.findAll();
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId,
                                         @Valid @RequestBody UserDto userDto) {
        log.info("update: user {} userId={}", userDto, userId);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("{userId}")
    public void deleteById(@PathVariable long userId) {
        log.info("deleteById {}", userId);
        userClient.deleteById(userId);
    }
}
