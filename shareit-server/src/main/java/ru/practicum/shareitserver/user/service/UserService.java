package ru.practicum.shareitserver.user.service;

import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.model.User;

import java.util.List;

public interface UserService {
    User save(User user);

    User findById(long userId);

    List<User> findAll();

    User update(long userId, UserDto userDto);

    void deleteById(long userId);
}
