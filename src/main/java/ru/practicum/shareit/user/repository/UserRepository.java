package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    User create(User user);

    Optional<User> getById(long userId);

    List<User> getAll();

    void deleteById(long userId, User user);

    Set<String> getEmails();
}
