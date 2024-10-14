package ru.practicum.shareitserver.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.exception.user.EmailAlreadyExistException;
import ru.practicum.shareitserver.exception.user.UserNotFoundException;
import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User save(User user) {
        log.info("UserServiceImpl: attempting to save user with email: {}", user.getEmail());
        if (isEmailExist(user.getEmail())) {
            log.warn("UserServiceImpl: email already exists: {}", user.getEmail());
            throw new EmailAlreadyExistException("Email: " + user.getEmail() + " already exist");
        }
        User savedUser = userRepository.save(user);
        log.info("UserServiceImpl: user saved successfully with email: {}", savedUser.getEmail());
        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(long userId) {
        log.info("UserServiceImpl: attempting to find user with id: {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("UserServiceImpl: user not found with id: {}", userId);
            return new UserNotFoundException("User with id " + userId + " not found");
        });

        log.info("UserServiceImpl: successfully found user with id: {}", userId);
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        log.info("UserServiceImpl: attempting to retrieve all users");
        List<User> users = userRepository.findAll();

        log.info("UserServiceImpl: retrieved {} users", users.size());
        return users;
    }

    @Override
    @Transactional
    public User update(long userId, UserDto userDto) {
        log.info("UserServiceImpl: attempting to update user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("UserServiceImpl: user not found with id: {}", userId);
                    return new UserNotFoundException("User with id " + userId + " not found");
                });

        log.info("UserServiceImpl: validating email for user with id: {}", userId);
        validateEmail(userDto.getEmail());

        log.info("UserServiceImpl: updating details for user with id: {}", userId);
        updateUserDetails(user, userDto);

        User updatedUser = userRepository.save(user);
        log.info("UserServiceImpl: successfully updated user with id: {}", userId);

        return updatedUser;
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
    @Transactional
    public void deleteById(long userId) {
        log.info("UserServiceImpl: attempting to delete user with id: {}", userId);

        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("UserServiceImpl: user not found with id: {}", userId);
            return new UserNotFoundException("User with id " + userId + " not found");
        });

        userRepository.deleteById(userId);
        log.info("UserServiceImpl: successfully deleted user with id: {}", userId);
    }

    private boolean isEmailExist(String email) {
        return userRepository.findDistinctEmails().contains(email);
    }
}