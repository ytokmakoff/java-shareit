package ru.practicum.shareitgateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BaseClient;
import ru.practicum.shareitgateway.user.dto.User;
import ru.practicum.shareitgateway.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<User> save(@RequestBody User user) {
        ResponseEntity<Object> response = post("", user);
        User userEntity = (User) response.getBody();
        return new ResponseEntity<>(userEntity, response.getStatusCode());
    }

    public ResponseEntity<User> findById(long userId) {
        ResponseEntity<Object> response = get("/" + userId);
        User userEntity = (User) response.getBody();
        return new ResponseEntity<>(userEntity, response.getStatusCode());
    }

    public ResponseEntity<User> findAll() {
        ResponseEntity<Object> response = get("");
        User user = (User) response.getBody();
        return new ResponseEntity<>(user, response.getStatusCode());
    }

    public ResponseEntity<User> update(long userId, UserDto userDto) {
        ResponseEntity<Object> response = patch("/" + userId, userDto);
        User user = (User) response.getBody();
        return new ResponseEntity<>(user, response.getStatusCode());
    }

    public ResponseEntity<User> deleteById(long userId) {
        ResponseEntity<Object> response = delete("/" + userId);
        User user = (User) response.getBody();
        return new ResponseEntity<>(user, response.getStatusCode());
    }
}