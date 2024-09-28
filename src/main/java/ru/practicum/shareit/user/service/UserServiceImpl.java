package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.user.EmailAlreadyExistException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        if (isEmailExist(user.getEmail()))
            throw new EmailAlreadyExistException("Email: " + user.getEmail() + " already exist");
        return userRepository.save(user);
    }

    @Override
    public User findById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        validateEmail(userDto.getEmail());

        updateUserDetails(user, userDto);

        return userRepository.save(user);
    }

    private void validateEmail(String email) {
        if (email != null && isEmailExist(email)) {
            throw new EmailAlreadyExistException("Email: " + email + " already exist");
        }
    }

    private void updateUserDetails(User user, UserDto userDto) {
        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
    }

    @Override
    public void deleteById(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        userRepository.deleteById(userId);
    }

    private boolean isEmailExist(String email) {
        return userRepository.findDistinctEmails().contains(email);
    }
}