package ru.practicum.shareitserver.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.exception.user.EmailAlreadyExistException;
import ru.practicum.shareitserver.exception.user.UserNotFoundException;
import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@Transactional
public class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void saveUserSuccessfully() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");

        User savedUser = userService.save(user);

        TypedQuery<User> query = em.createQuery("select u from User u where u.id = :id", User.class);
        User foundUser = query.setParameter("id", savedUser.getId()).getSingleResult();

        assertThat(foundUser.getId(), notNullValue());
        assertThat(foundUser.getName(), equalTo(user.getName()));
        assertThat(foundUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void saveUserWithExistingEmailSuccessfully() {
        User user1 = new User();
        user1.setName("Bob");
        user1.setEmail("bob@example.com");
        userService.save(user1);

        User user2 = new
                User();
        user2.setName("Charlie");
        user2.setEmail("bob@example.com");

        assertThatThrownBy(() -> userService.save(user2))
                .isInstanceOf(EmailAlreadyExistException.class)
                .hasMessage("Email: " + user2.getEmail() + " already exist");
    }

    @Test
    void findByIdSuccessfully() {
        User user = new User();
        user.setName("David");
        user.setEmail("david@example.com");
        User savedUser = userService.save(user);

        User foundUser = userService.findById(savedUser.getId());

        assertThat(foundUser.getId(), equalTo(savedUser.getId()));
        assertThat(foundUser.getName(), equalTo(savedUser.getName()));
        assertThat(foundUser.getEmail(), equalTo(savedUser.getEmail()));
    }

    @Test
    void findByIdNotFoundSuccessfully() {
        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with id 999 not found");
    }

    @Test
    void findAllSuccessfully() {
        User user1 = new User();
        user1.setName("Eve");
        user1.setEmail("eve@example.com");
        userService.save(user1);

        User user2 = new User();
        user2.setName("Frank");
        user2.setEmail("frank@example.com");
        userService.save(user2);

        List<User> users = userService.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail).containsExactlyInAnyOrder("eve@example.com", "frank@example.com");
    }

    @Test
    void updateUserSuccessfully() {
        User user = new User();
        user.setName("Grace");
        user.setEmail("grace@example.com");
        User savedUser = userService.save(user);

        UserDto userDto = new UserDto();
        userDto.setName("Grace Updated");
        userDto.setEmail("grace.updated@example.com");

        User updatedUser = userService.update(savedUser.getId(), userDto);

        assertThat(updatedUser.getId(), equalTo(savedUser.getId()));
        assertThat(updatedUser.getName(), equalTo(userDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(userDto.getEmail()));

        TypedQuery<User> query = em.createQuery("select u from User u where u.id = :id", User.class);
        User foundUser = query.setParameter("id", updatedUser.getId()).getSingleResult();

        assertThat(foundUser.getName(), equalTo(userDto.getName()));
        assertThat(foundUser.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUserWithExistingEmail() {
        User user1 = new User();
        user1.setName("Hannah");
        user1.setEmail("hannah@example.com");
        userService.save(user1);

        User user2 = new User();
        user2.setName("Ian");
        user2.setEmail("ian@example.com");
        userService.save(user2);

        UserDto userDto = new UserDto();
        userDto.setEmail("hannah@example.com");

        assertThatThrownBy(() -> userService.update(user2.getId(), userDto))
                .isInstanceOf(EmailAlreadyExistException.class)
                .hasMessage("Email: hannah@example.com already exist");
    }

    @Test
    void deleteByIdSuccessfully() {
        User user = new User();
        user.setName("Jack");
        user.setEmail("jack@example.com");
        User savedUser = userService.save(user);

        userService.deleteById(savedUser.getId());

        assertThatThrownBy(() -> userService.findById(savedUser.getId()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with id " + savedUser.getId() + " not found");
    }

    @Test
    void deleteByIdNotFound() {
        assertThatThrownBy(() -> userService.deleteById(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with id 999 not found");
    }
}
