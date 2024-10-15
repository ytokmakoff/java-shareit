package ru.practicum.shareitgateway.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareitgateway.user.dto.User;
import ru.practicum.shareitgateway.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_shouldCallUserClientSave() {
        User user = new User();
        user.setName("John Doe");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(userClient.save(user)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userController.save(user);

        assertEquals(expectedResponse, actualResponse);
        verify(userClient).save(user);
    }

    @Test
    void findById_shouldCallUserClientFindById() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(userClient.findById(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userController.findById(userId);

        assertEquals(expectedResponse, actualResponse);
        verify(userClient).findById(userId);
    }

    @Test
    void findAll_shouldCallUserClientFindAll() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(userClient.findAll()).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userController.findAll();

        assertEquals(expectedResponse, actualResponse);
        verify(userClient).findAll();
    }

    @Test
    void update_shouldCallUserClientUpdate() {
        long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("Jane Doe");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(userClient.update(userId, userDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userController.update(userId, userDto);

        assertEquals(expectedResponse, actualResponse);
        verify(userClient).update(userId, userDto);
    }

    @Test
    void deleteById_shouldCallUserClientDeleteById() {
        long userId = 1L;

        userController.deleteById(userId);

        verify(userClient).deleteById(userId);
    }
}
