package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User save(User user);

    User findById(long userId);

    List<User> findAll();

    User update(long userId, UserDto userDto);

    void deleteById(long userId);
}
