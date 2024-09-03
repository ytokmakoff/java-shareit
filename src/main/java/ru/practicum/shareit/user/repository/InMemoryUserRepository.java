package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @Override
    public User create(User user) {
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public Optional<User> getById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user, UserDto userDto) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            emails.remove(user.getEmail());
            emails.add(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(long userId, User user) {
        emails.remove(user.getEmail());
        users.remove(userId);
    }

    @Override
    public Set<String> getEmails() {
        return emails;
    }
}
