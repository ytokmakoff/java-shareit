package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    User create(User user);

    User getById(long userId);

    List<User> getAll();

    User update(long userId, UserDto userDto);

    void deleteById(long userId);
}
