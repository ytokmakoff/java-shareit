package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private long id = 0;

    @Override
    public User create(User user) {
        if (isEmailContains(user.getEmail()))
            throw new EmailAlreadyExistException("Email: " + user.getEmail() + " already exist");
        user.setId(generateId());
        return userRepository.create(user);
    }

    @Override
    public User getById(long userId) {
        return userRepository.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User update(long userId, UserDto userDto) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        if (userDto.getEmail() != null && isEmailContains(userDto.getEmail())) {
            throw new EmailAlreadyExistException("Email: " + userDto.getEmail() + " already exist");
        }

        return userRepository.update(user, userDto);
    }

    @Override
    public void deleteById(long userId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        userRepository.deleteById(userId, user);
    }

    private boolean isEmailContains(String email) {
        return userRepository.getEmails().contains(email);
    }

    private long generateId() {
        return ++id;
    }
}