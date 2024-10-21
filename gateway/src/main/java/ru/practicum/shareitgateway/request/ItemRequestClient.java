package ru.practicum.shareitgateway.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BaseClient;
import ru.practicum.shareitgateway.request.dto.ItemRequest;
import ru.practicum.shareitgateway.request.dto.ItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<ItemRequest> save(ItemRequest itemRequest, long userId) {
        ResponseEntity<Object> response = post("", userId, itemRequest);
        ItemRequest itemRequestEntity = (ItemRequest) response.getBody();
        return new ResponseEntity<>(itemRequestEntity, response.getStatusCode());
    }

    public ResponseEntity<ItemRequestDto> findByUserId(long userId) {
        ResponseEntity<Object> response = get("", userId);
        ItemRequestDto itemRequestDto = (ItemRequestDto) response.getBody();
        return new ResponseEntity<>(itemRequestDto, response.getStatusCode());
    }

    public ResponseEntity<ItemRequest> findAll(long userId) {
        ResponseEntity<Object> response = get("/all", userId);
        ItemRequest itemRequest = (ItemRequest) response.getBody();
        return new ResponseEntity<>(itemRequest, response.getStatusCode());
    }

    public ResponseEntity<ItemRequestDto> findById(long requestId) {
        ResponseEntity<Object> response = get("/" + requestId);
        ItemRequestDto itemRequestDto = (ItemRequestDto) response.getBody();
        return new ResponseEntity<>(itemRequestDto, response.getStatusCode());
    }
}